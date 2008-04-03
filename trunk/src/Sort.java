
public class Sort  implements IteratorInterface {

	/**
	 * @param args
	 */
	
	Iterator iterator;
	// public void open(RelationInfo R, String [] where, Index idx)
	public void open(BufferManager bm, RelationInfo R, String [] where)
	{
	}
	
	public Tuple next()
	{
		return iterator.getNext();
	}
}