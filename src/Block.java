/**
 * @author chrisb
 *
 */
public class Block {

	public byte[] content;
	
	/**
	 * With isPin, isUpdated and blockID inside this class, 
	 * the address allocation table is unnecessary.
	 */		
	public boolean isPinned = false;
	
	/**
	 * The blockID to which this block reference.  The first
	 * 4 bytes of the variable is the file number.  the second
	 * 4 bytes is the offset of the block in the file on disk.
	 */ 
	public long blockID = -1;
	
	/**
	 * This flag should be set to true when a transaction updates the block 
	 * and writes it to the buffer.
	 */
	private boolean isUpdated = false;
	
	// set record number to 0
	public Block()
	{
		content = new byte[Parameters.BLOCK_SIZE];
		byte [] header = Utility.makeByte4FromInt(0);
		content[0] = header[0];
		content[1] = header[1];
		content[2] = header[2];
		content[3] = header[3];
	}
	
	public Block(long blockID, byte[] content) {
		this.blockID = blockID;
		this.content = content;
	}
	
	/**
	 * it returns the number of record in the block. 
	 * @return
	 */
	public int getRecordNumber()
	{
		byte [] byteArray = new byte[4];
		byteArray[0] = this.content[0];
		byteArray[1] = this.content[1];
		byteArray[2] = this.content[2];
		byteArray[3] = this.content[3];
		return Utility.makeIntFromByte4(byteArray);
	}
	
	/**
	 * It updates the number of record in the block
	 */
	public void updateRecordNumber(int blockNum)
	{
		byte [] byteArray = new byte[4];
		byteArray[0] = this.content[0];
		byteArray[1] = this.content[1];
		byteArray[2] = this.content[2];
		byteArray[3] = this.content[3];
		int recNum = Utility.makeIntFromByte4(byteArray);
		recNum = recNum + blockNum;
		byteArray = Utility.makeByte4FromInt(recNum);
		content[0] = byteArray[0];
		content[1] = byteArray[1];
		content[2] = byteArray[2];
		content[3] = byteArray[3];
	}

	public int getBlockNumber()
	{
		int[] temp = Utility.split(this.blockID);
		return temp[1];
	}
	
	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public boolean isPinned() {
		return isPinned;
	}

	public void setPinned(boolean isPinned) {
		this.isPinned = isPinned;
	}

	public long getBlockID() {
		return blockID;
	}

	public void setBlockID(long blockID) {
		this.blockID = blockID;
	}

	public boolean isUpdated() {
		return isUpdated;
	}

	public void setUpdated(boolean isUpdated) {
		this.isUpdated = isUpdated;
	}
	
	/**
	 * append data to the block that has free space for it to be write it back to file
	 * It will be used by insert query
	 * @param data
	 */
	public void writeToBlock(byte [] data)
	{
		this.isUpdated = true;
		this.isPinned = true;
		int recNum = this.getRecordNumber();
		int pos = data.length * recNum;
		for (int i = 0; i < data.length; i++)
		{
			this.content[pos+i] = data[i];
		}
		this.updateRecordNumber(1);
		this.isPinned = false;
	}
	
	/**
	 * return array of byte inside the block
	 * @param offset
	 * @param len
	 * @return
	 */
	public byte [] getTupleContent(int offset, int len)
	{
		byte [] data = new byte[len];
		
		for (int i = 0; i < len; i++)
		{
			data[i] = content[offset + i];
		}
		return data;
	}
	
	public void printBlock ()
	{
		System.out.println ("ID: " + this.blockID);
		System.out.println ("Updated: " + this.isUpdated);
		System.out.println ("pinned: " + this.isPinned);
		for (int i = 0; i < Parameters.BLOCK_SIZE; i++) {
			System.out.print (" Byte # " + i + ": " + (char) content[i]);
			if (i % 128 == 0)
			{
				System.out.println();
			}
		}
	}
}
