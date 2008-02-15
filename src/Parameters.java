/**
 * 
 */

/**
 * @author chrisb
 *
 */
public class Parameters {

	public static int BLOCK_SIZE=512;
	public static int NUM_BLOCK_BUFFER=200;
	public static int BTREE_ORDER = 5;//example
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		System.out.println(BLOCK_SIZE);
		System.out.println(NUM_BLOCK_BUFFER);
		System.out.println(BTREE_ORDER);

	}

}
