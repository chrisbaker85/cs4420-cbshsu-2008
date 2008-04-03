
public class IndexScan implements IteratorInterface {

	/**
	 * @param args
	 */
	
	IndexInfo index; 
	Iterator iterator;
	// it search for the tuple in the index using where clause
	// where must be the key
	// i.e age is key (age > 20 and age < 60) or (age = 25)
 	public void open(BufferManager bm, RelationInfo R, String [] where)
	{
		//index = R.getIndex();
		// create an index
 		// if we have to 
	}
	
	public Tuple next()
	{
		return iterator.getNext();
	}
	
}
