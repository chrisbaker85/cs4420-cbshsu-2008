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
	
	/**
	 * The Block object in which this tuple is contained
	 */
	protected Block block;
	
	/**
	 * The location of this tuple in the block.
	 */ 
	protected int offset;
	
	/**
	 * Class Constructor
	 * @param offset the block offset
	 * @param block the block it is contained in
	 * @param info the RelationInfo for this record
	 */
	public Tuple(int offset, Block block, RelationInfo info) {
		
		this.block = block;
		this.offset = offset;
		this.info = info;
		
	}
	
	public Tuple() {
		
		
	}
	
	/**
	 * 
	 * This method may be needed later.  Currently returns -1
	 * @param i ith field in this tuple
	 * @return -1
	 */
	public String getField(int i) {
		return "";
	}
	
	/**
	 * Obtain the value of ith column. It is the responsibility of
	 * the system/user to determine which data type should be returned.
	 */ 
	public int getInt(int i) {
		
		// Find where the field is in relation to the other fields
		int tupleOffset = this.info.getFieldOffset(i);
		byte[] data = new byte[4];
		int j;
		
		for (j = 0; j < 4; j++) {
		
			data[j] = this.block.content[tupleOffset + this.offset];
			
		}
		
		return Utility.makeIntFromByte4(data);
		
	}
	
	/**
	* Obtain the value of ith column
	*/
	public String getString(int i){

		// Use the Block object to find the record
		
		// Once the Tuple data is found, use the
		// RelationInfo object to find the bytes to return
		
		return "x";
		
	}	
	
	/**
	 * When an attribute of the tuple is updated,
	 * you have to let the buffer aware that the block containing this
	 * tuple is updated.
	 */ 
	public void putInt(int i, int value) {
		
		// Find where the field is in relation to the other fields
		int tupleOffset = this.info.getFieldOffset(i);
		byte[] data = Utility.makeByte4FromInt(value);
		int j;
		
		for (j = 0; j < 4; j++) {
		
			this.block.content[tupleOffset + this.offset] = data[j];
			
		}
		
		this.block.isUpdated();
		
	}
	
	public void putString(int column, int value) {
		
		// update the bytes in the block
		
		// update the block's isUpdated flag
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
