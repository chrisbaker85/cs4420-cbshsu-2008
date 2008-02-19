/**
 * @author: Minh Quoc Nguyen
 *
 * Read this comment carefully to know the purpose of the methods and when 
 * they should be called.
 * 
 * This class provides the functionalities which will be called by the 
 * storage manager/query processor.
 * 
 * - pin     : pin the block so that the buffer manager can not replace it with other blocks.
 * - unpin   : release the pin
 * - getBlock: get a block from the buffer for a transaction that needs it.
 * - flush   : write all blocks that are modified by the transactions to disc
 *
 * Internally, the buffer manager should contain:
 * - Buffer : where the blocks are stored
 * - Lookup Table : refer to the slide from the class website for detail.
 * 
 * The following operation is for internal use:
 * - readBlock : allow the buffer to read the block with a given ID from file.
 * - writeBlock: write the block from the buffer to disc
 * - setStatus : if the method putBlock is issued, the status of the block must be set to UPDATED
 * - nextSlot  : indicates which block in the buffer is ready to be replaced by a new block.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Hashtable;

public class BufferManager {
	int num_buffer = 10;

	/**
	 * This is the buffer to store and load the data. You need to determine the
	 * size of the buffer = num_block * block_size. Don't forget to initialize
	 * the buffer by invoking the method initialize()
	 */
	Block[] buffer = null;

	Hashtable<Long, Integer> lookupTable = null;

	public void initialize() {
		// initialize the buffer
		buffer = new Block[Parameters.NUM_BLOCK_BUFFER];
		// initialize the lookupTable
		lookupTable = new Hashtable<Long, Integer>();
	}

	/**
	 * Flush all the blocks which were modified to the discs.
	 */
	public void flush() {

		Block temp = null;
		
		int i;

		// iterate through the buffer
		for (i = 0 ; i < Parameters.NUM_BLOCK_BUFFER; i++) {

			temp = buffer[i];

			// check if block has been updated and write block to disk
			if (temp != null && temp.isUpdated) {
				writeBlock(buffer[i].blockID);
			}
		}
	}

	/**
	 * Pin a block. If a block is pinned, it will not be replaced by another
	 * block. It will stay in the memory. We need this operation for some
	 * special blocks such as: the root nodes of the index trees.
	 */
	public boolean pin(long blockID) {
		
		if (!lookupTable.contains(blockID))
			return false;

		int slot_num = lookupTable.get(blockID);

		buffer[slot_num].setPinned(true);
		return true;
	}

	/**
	 * Unpin the block.
	 */
	public boolean unpin(long blockID) {
		
		if (!lookupTable.contains(blockID))
			return false;

		int slot_num = lookupTable.get(blockID);

		buffer[slot_num].setPinned(false);
		return true;
	}

	/**
	 * Return the block to a transaction that wants it. First check if the block
	 * exists in the buffer. - If yes, return the block - Otherwise, read the
	 * block into the buffer and return this block
	 */
	public Block getBlock(long blockID) {
		
		if (!lookupTable.contains(blockID))
			readBlock(blockID);

		int slot_num = ((Integer)lookupTable.get(blockID)).intValue();

		return buffer[slot_num];
	}

	/**
	 * Return the next slot that a new block should be written to. If there is
	 * no avaible block, write any block with the status "updated" to the disc
	 * and free that block. This method contains the slot location policy which
	 * will determine which block should be written to the disc to yield the
	 * space to a new block if the buffer is full. The policy can be FIFO, LIFO,
	 * Random etc. You need to modify the block structure a little bit to fit
	 * your design.
	 */
	private int nextSlot() {

		int i = 0;
		Block temp = null;

		while (i < Parameters.NUM_BLOCK_BUFFER) {
			if (buffer[i] == null) {
				return i;
			}
			i++;
		}

		i = 0;

		while (i < Parameters.NUM_BLOCK_BUFFER) { // iterate through the
			// buffer and not pinned
			temp = buffer[i];
			if (temp != null) {
				if (temp.isUpdated && !temp.isPinned) { // check if block has
					// been updated
					writeBlock(buffer[i].blockID); // write block to disk
					lookupTable.remove(temp.blockID); // evict block
					buffer[i] = null;
					return i;
				}
			}
		}

		return -1;

	}

	/**
	 * Read a block with the given block ID into the memory.
	 */
	private void readBlock(long blockID) {

		int slot_num = -1;

		if (lookupTable.contains(blockID)) {
			slot_num = ((Integer) lookupTable.get(blockID)).intValue();
		} else {
			slot_num = nextSlot();
		}

		// - Use the function Utility.split(blockID) to determine the the
		// filename
		// and the relative block_num.
		// - From the block_num + filename, copy the corresponding byte array
		// from
		// the file into a block.
		// - Fill in the block information.
		// - Put the block to the given slot_num.
		// - Update the lookupTable.
		// - Hint: to avoid the overhead of opening files so many times, create
		// an array
		// keep the pointers to the opened files.

		if (slot_num != -1)
		{
			try 
			{
				int[] split = Utility.split(blockID);
				String fileNameID = "" + split[0];
				int offSet = split[1];

				RandomAccessFile fileIn = new RandomAccessFile(fileNameID, "rw");
				FileChannel fileChannel = fileIn.getChannel();
				MappedByteBuffer tempBuffer = fileChannel.map(
						FileChannel.MapMode.READ_WRITE, 0, fileIn.length());
				fileChannel.read(tempBuffer);

				byte[] temp = new byte[Parameters.BLOCK_SIZE];

				for (int i = 0; i < Parameters.BLOCK_SIZE; i++) {
					temp[i] = tempBuffer.get(i + offSet);
				}

				Block newBlock = new Block(blockID, temp);
				lookupTable.put(blockID, new Integer(slot_num));
				buffer[slot_num] = newBlock;

				fileIn.close();
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Write a block whose status is updated from the memory to the disc. Then
	 * update the status of the block to "not updated" since the copy of the
	 * block in the memory is the same as that in the disc. If the status is not
	 * updated, it does nothing.
	 */
	private void writeBlock(long blockID) {
		
		Block temp = null;

		if (lookupTable.contains(blockID))
		{
			int slot_num = ((Integer) lookupTable.get(blockID)).intValue();
			temp = buffer[slot_num];

			if (temp != null)
			{
				try 
				{
					int[] split = Utility.split(temp.blockID);
					String fileNameID = "" + split[0];
					int offSet = split[1];
					RandomAccessFile fileOut = new RandomAccessFile(fileNameID, "rw");
					FileChannel fileChannel = fileOut.getChannel();
					MappedByteBuffer tempBuffer = fileChannel.map(
							FileChannel.MapMode.READ_WRITE, offSet,
							Parameters.BLOCK_SIZE);
					
					tempBuffer.put(temp.getContent());
					temp.setUpdated(false);
					
					fileOut.close();
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

	/**
	 * if a block with blockID is updated, we should set the status of the block
	 * to be updated . This status is set when the block is modified, i.e,
	 * add/update/remove a new tuple or add/update/remove key/pointer
	 * 
	 */
	private void setStatus(long blockID)
	{
		if (lookupTable.contains(blockID))
		{

			int slot_num = ((Integer)lookupTable.get(blockID)).intValue();
			buffer[slot_num].setUpdated(true);
		}
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		// System.out.println("just test");
		// System.out.println("craate database: doing nothing");
		// System.out.println("create table student");
		// FileWriter student = new FileWriter(new File("student.dat"), true);
	}
}