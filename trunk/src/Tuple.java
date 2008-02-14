/**
 * 
 */

/**
 * @author chrisb
 *
 */
public class Tuple {

	/**
	 * It contains the detail of the relation of this tuple.
	 * This information guide how to pass a byte array into 
	 * column. The parsing can be done on demand.	 	 	 
	 */ 
	protected RelationInfo info; 
	
	protected Block block;
	
	/**
	 * The location of this tuple in the block.
	 */ 
	protected int   offset;
		
	/**
	 * Obtain the value of ith column. It is the responsibility of
	 * the system/user to determine which data type should be returned.
	 */ 
	public int getInt(int i){
		
		return -1;
		
	}
	
	/**
	* Obtain the value of ith column
	*/
	public String getString(int i){
		
		return "x";
		
	}	
	
	/**
	 * When an attribute of the tuple is updated,
	 * you have to let the buffer aware that the block containing this
	 * tuple is updated.
	 */ 
	public void putInt(int column, int value){}
	
	public void putString(int column, int value){}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
