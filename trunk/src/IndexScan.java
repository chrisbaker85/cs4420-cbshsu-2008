import java.util.Hashtable;
import java.util.SortedMap;
import java.util.TreeMap;


public class IndexScan implements IteratorInterface {

	/**
	 * @param args
	 */
	
	IndexInfo index; 
	Iterator iterator;
	RelationInfo R;
	Main main;
	BufferManager bm;
	String [] condition;
	
	public IndexScan(Main main, BufferManager bm, RelationInfo R, String [] condition)
	{
		main = main;
		bm = bm;
		R = R;
		condition = condition;
	}
 	public void open(BufferManager bm, RelationInfo R, String [] where)
	{
 		String tempRelation = R.getName() + "indexscaned";
 		Hashtable attHash = R.getAttribute();
		String [] attNames = Utility.getAttributeNames(attHash);
		String [][] atts = new String[attNames.length][4];
		for (int i = 0; i < attNames.length; i++)
		{
			Attribute att = (Attribute)attHash.get(attNames[i]);
			atts[i][0] = attNames[i];
			atts[i][1] = att.getType();
			atts[i][2] = att.getLength();
			atts[i][3] = att.getIsNullable();
		}
		// create a temporary relation
		main.createTable(bm.getDBName(), tempRelation, atts);
		
		// get index information
		TreeMap index = R.getIndexInfo().getIndex();
		// check the type of operation ( >, < or =)
		if (where[1].equals(">"))
		{
			// get the sorted key larder than specified value 
			SortedMap sortedmap = index.tailMap(where[2]);
			// TODO: to complete below
			/**
			 * 1. get the offsets
			 * 2. get the tuple using the offset
			 * 3. insert tuple into tempRelation using main.insertQuery()
			 */
		} 
		if (where[1].equals("="))
		{
			// TODO: to complete below
		
			/**
			 * 1. get the offset using TreeMap.get()
			 * 2. convert it to tuple 
			 * 3. insert tuple into tempRelation using main.insertQuery()
			 */
		}
		if (where[1].equals("<"))
		{
			// get the sorted key larder than specified value 
			SortedMap sortedmap = index.headMap(where[2]);
			// TODO: to complete below
			/**
			 * 1. get the offsets
			 * 2. get the tuple using the offset
			 * 3. insert tuple into tempRelation using main.insertQuery()
			 */
		}
	}
	
	public Tuple next()
	{
		return iterator.getNext();
	}
	
}
