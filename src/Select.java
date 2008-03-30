/**
 * 
 */

/**
 * @author Sovandy
 *
 */
public class Select implements IteratorInterface {

	/**
	 * @param args
	 */
	
	Iterator iterator;
	// public void open(RelationInfo R, String [] where, Index idx)
	public void open(BufferManager bm, RelationInfo R, String [] where)
	{
		RelationInfo result = new RelationInfo();
		// set attributes here
		
		Iterator tempIterator = new Iterator(bm, R, R.getId(), Integer.parseInt(R.getNumDataBlocks()));
		for (int i = 0; i < Integer.parseInt(R.getNumTuples().trim()); i++)
		{
			// using RelationInfo, insert a new tuple one by one like insert in Main
		}
		
		iterator = new Iterator(bm, result, result.getId(), Integer.parseInt(result.getNumDataBlocks()));
	}
	
	public Tuple next()
	{
		return iterator.getNext();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
