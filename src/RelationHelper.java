/**
 * 
 */

/**
 * @author chrisb
 *
 */
public class RelationHelper {

	/**
	 * This method reads a block and then returns all the tuples
	 * inside the block.
	 * 
	 * From the blockID, we obtain the data file name (by using the Utility.split)
	 * function. This filename can be used as an unique idenfitier 
	 * to obtain the relation information.
	 * 
	 * The relation information is then used to construct the tuples:
	 * - A simple method is to use the relation information in order to know
	 * the tuple length of the relation.
	 * - The header of the block is used to determine how many tuples in 
	 * the block.
	 * - Create new tuples according to the number of tuple value.
	 * - Put the block, relation information and the beginning offset of 
	 * the tuple in the block into the tuples.
	 * 
	 * DONT forget to pin the block if it is in use and unpin the block,
	 * if it's not.
	 */ 
	public static Tuple[] scanBlock(Block block)
	{
		// TODO: fix me!
		Tuple[] temp = {new Tuple()};
		return temp;
	}
		
	/**
	 * Given a block and a primary key, find the corresponding tuple.
	 * You may want to define key as a generic data type so that you 
	 * don't have to write different method method for different data type.
	 * Of course, the class tuple must be modified accordingly.
	 * Hint: use the scanBlock to extract all the tuples and then use the key
	 * to return the tuple.
	 */ 
	public Tuple  extractTupleFrom(Block block, int primkey)
	{
		
		return new Tuple();
		
	}
		
	/**
	 * Given a relationInfo and key. Find the tuple.
	 * - If there is no index on this key, you need to perform a table scan which 
	 *   will scan from the beginning of the data file until the tuple that contains
	 *   some values in that column.
	 * - If there is an index, use the index to find the tuples.
	 */	  
	public Tuple[]  findTuple(RelationInfo relationInfo, Object value, int column)
	{
		
		Tuple[] temp = {new Tuple()};
		return temp;
		
	}
		
	public Tuple  findTuple(RelationInfo relationInfo, int key)
	{
		
		return new Tuple();
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
