/**
 * 
 */

/**
 * @author chrisb
 *
 */
public class Parameters {

	// 1KB block size
	public static int BLOCK_SIZE=1024;
	
	// The buffer pool is 16MB with 1KB blocks
	public static int NUM_BLOCK_BUFFER=4096;
	
	// The order of the B+Tree
	public static int BTREE_ORDER = 5;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		System.out.println("Block Size: " + BLOCK_SIZE);
		System.out.println("# of Blocks in the Buffer: " + NUM_BLOCK_BUFFER);
		System.out.println("B+Tree Order:" + BTREE_ORDER);

	}

}
