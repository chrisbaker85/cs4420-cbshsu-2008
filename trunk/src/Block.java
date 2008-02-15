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
	
	public static int SIZE = 512;
	
	public Block()
	{}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}