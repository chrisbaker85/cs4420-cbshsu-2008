
public class TableScan implements IteratorInterface {

	/**
	 * @param args
	 */
	
	Iterator iterator;
	RelationInfo R;
	BufferManager bm;
	
	public TableScan(BufferManager bm, RelationInfo R)
	{
		bm = bm;
		R = R;
	}
	// public void open(RelationInfo R, String [] where, Index idx)
	public void open(BufferManager bm, RelationInfo R, String [] where)
	{
	}
	
	public Tuple next()
	{
		return iterator.getNext();
	}
}
