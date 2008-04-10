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
	Main main;
	
	public TableScan(Main main, RelationInfo R)
	{
		this.main = main;
		this.R = R;
	}
	// public void open(RelationInfo R, String [] where, Index idx)
	public RelationInfo open()
	{
		iterator = new Iterator(main.getBm(), R, R.getId(), Integer.parseInt(R.getNumDataBlocks()));
		return R;
		
	}
	
	public Tuple next()
	{
		return iterator.getNext();
	}
}
