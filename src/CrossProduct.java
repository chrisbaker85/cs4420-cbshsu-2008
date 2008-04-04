/**
 * 
 */

/**
 * @author Sovandy Hang
 *
 */
public class CrossProduct implements IteratorInterface{

	/**
	 * @param args
	 */
	Iterator iterator;
	Main main;
	BufferManager bm;
	RelationInfo R1;
	RelationInfo R2;
	
	public CrossProduct(Main main, BufferManager bm, RelationInfo R1, RelationInfo R2)
	{
		main = main;
		bm = bm;
		R1 = R1;
		R2 = R2;
	}
	public void open()
	{
		String tempRelation = R1.getColsIndexed() + "_CROSS_" + R2.getName();
		/**
		 * 1. get attributes of R1 and R2
		 * 2. compare and merge them
		 * 3. create table for that relation
		 * OPERATION
		 * ---------
		 * 1. In table 1, get one tuple at a time
		 * 2. select from table 2 with condition of field = value of table 1
		 * 3. insert into new table
		 */ 
		
	}
	
	public Tuple next()
	{
		return iterator.getNext();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
