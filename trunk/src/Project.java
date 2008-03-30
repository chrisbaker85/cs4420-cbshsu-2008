/**
 * 
 */

/**
 * @author Sovandy
 *
 */
public class Project implements IteratorInterface {

	/**
	 * @param args
	 */
	
	Iterator iterator;
	// RelationInfo result;
	public void open(BufferManager bm, RelationInfo R, String [] atts)
	{
		RelationInfo result = new RelationInfo();
		// set attributes here: dateCreated, dateModified, number of tuples, number of blocks etc...
		// set attribute to custom attributes atts
		
		// tempIterator: iterator for R to be projected
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
