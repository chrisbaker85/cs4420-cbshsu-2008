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
		main = main;
		R = R;
	}
	// public void open(RelationInfo R, String [] where, Index idx)
	public void open()
	{
		iterator = new Iterator(main.getBm(), R, R.getId(), Integer.parseInt(R.getNumDataBlocks()));
	}
	
	public Tuple next()
	{
		return iterator.getNext();
	}
}
