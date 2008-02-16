/**
 * 
 */

/**
 * @author chrisb
 *
 */
public class Utility {

	/**
	 * Return a block ID from fileID and the block number relative to 
	 * the beginning of the file.
	 * 
	 * REMEMBER: to make it easy. I recommend you to use autoincrement 
	 * number to name the data files and index files.
	 * Then the file number can be combined with block number as a unique 
	 * identifier of a block.
	 * 
	 * @Return - blockID
	 */ 
	public long combine(int fileID, int block_num) {
		
		return ((fileID & 0xffffffffL) << 32 | (block_num & 0xffffffffL));
		
	}
		
	/**
	 * Given a block ID, return the file ID (which is file name, i.e filename "1") 
	 * and the block number relative to the beginning of the file.
	 */ 
	public int[] split(long blockID)
	{
		int[] larr = {(int)(blockID >> 32), (int)blockID};
		return larr; 
		
	}
		
	/**
	 * Given block_number, compute the offset of the beginning of the block
	 * relative to the beginning of a file. 
	 * i.e. offset = block_num*BLOCK_SIZE
	 */
	public int offset(int block_num)
	{
		
		return 0;
		
	}
		
	/**
	 * Analyze the blockID and use the system catalog to obtain the relation
	 * info for the given blockID
	 */ 
	public RelationInfo getRelationInfo(int blockID)
	{
		
		return new RelationInfo();
		
	}

	/**
	 * Analyze the blockID and use the system catalog to obtain the index
	 * info for the given blockID
	 */ 
	public IndexInfo getIndexInfo(int blockID)
	{
		
		return new IndexInfo();
		
	}		
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	    Utility u = new Utility();
	    
	    Integer i1 = new Integer(1000);
	    Integer i2 = new Integer(512);
	    Long l = new Long(u.combine(i1, i2));
        int[] temp = u.split(l);
	    System.out.println("i1: " + Integer.toBinaryString(i1));
	    System.out.println("i2: " + Integer.toBinaryString(i2));
	    System.out.println(" l: " + Long.toBinaryString(l));
	    System.out.println(temp[0] + ", " + temp[1]);

	}

}
