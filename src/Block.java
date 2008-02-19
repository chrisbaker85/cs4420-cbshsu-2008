/**
 * 
 */

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
	 * The blockID to which this block reference. Check the class Block
	 * to see how to obtain the blockID.
	 */ 
	public long blockID = -1;
	
	/**
	 * This flag should be set to true when a transaction updates the block 
	 * and writes it to the buffer.
	 */
	public boolean isUpdated = false;
	
	public Block()
	{}
	
	public Block(long blockID, byte[] content) {
		this.blockID = blockID;
		this.content = content;
	}
	
	public int getRecordNumber()
	{
		// how to map from array to int? 
		return 0;
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
	
	public void printBlock ()
	{
		System.out.println ("ID: " + this.blockID);
		System.out.println ("Updated: " + this.isUpdated);
		System.out.println ("pinned: " + this.isPinned);
		for (int i = 0; i < Parameters.BLOCK_SIZE; i++) {
			System.out.println (" Byte # " + i + ": " + content[i]);
			if (i % 128 == 0)
			{
				System.out.println();
			}
		}
	}
}
