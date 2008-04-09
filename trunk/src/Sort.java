
public class Sort  implements IteratorInterface {

	/**
	 * @param args
	 */
	
	Iterator iterator;
	Main main;
	String [] where;
	RelationInfo R;
	public Sort(Main main, RelationInfo R, String [] where)
	{
		where = where;
		main = main;
		R = R;
	}
	public RelationInfo open()
	{
		return R;
	}
	
	public Tuple next()
	{
		return iterator.getNext();
	}
}