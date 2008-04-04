/**
 * @author Sovandy Hang
 *
 */

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
		iterator = new Iterator(bm, R, R.getId(), Integer.parseInt(R.getNumDataBlocks()));
	}
	
	public Tuple next()
	{
		return iterator.getNext();
	}
}
