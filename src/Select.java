import java.util.*;

/**
 * @author Sovandy
 *
 */
public class Select implements IteratorInterface {

	/**
	 * @param args
	 */
	
	Iterator iterator;
	BufferManager bm;
	RelationInfo R;
	String [] where;
	Main main;
	boolean index;
	
	public Select(Main main, BufferManager bm, RelationInfo R, String [] where, boolean index)
	{
		main = main;
		bm = bm;
		R = R;
		where = where;
		index = index;
	}
	
	// public void open(RelationInfo R, String [] where, Index idx)
	public void open()
	{
		String tempRelation = R.getName() + "selected";
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
		
		if (!index) // if index is specified
		{
			// get the position of condition in the array of attributes
			int ind;
			for (ind = 0; ind < attNames.length; ind++)
			{
				if (attNames[ind].equals(where[0])) break;
			}
			// use tablescan to interate through relation
			IteratorInterface iterator = new TableScan(bm, R);
			Tuple tuple;
			int tupleLength = Utility.getTotalLength(R.getAttribute());
			
			for (int i = 0; i < Integer.parseInt(R.getNumTuples().trim()); i++)
			{
				tuple = iterator.next();
				Block block = tuple.getBlock();
				int offset = tuple.getOffset();
				byte [] content = block.getTupleContent(offset, tupleLength);
				String [] results = Utility.convertTupleToArray(attHash, content);
				// compare it result with condition
				if (where[1].equals(">"))
				{
					if (Integer.parseInt(results[ind]) > Integer.parseInt(where[2]))
					{
						// TODO: insert the tuple here by calling main.insertQuery()
					}
				}
				if (where[1].equals("="))
				{
					if (Integer.parseInt(results[ind]) == Integer.parseInt(where[2]))
					{
						// TODO: insert the tuple here by calling main.insertQuery()
					}
				}
				if (where[1].equals("<"))
				{
					if (Integer.parseInt(results[ind]) < Integer.parseInt(where[2]))
					{
						// TODO: insert the tuple here by calling main.insertQuery()
						
					}
				}
			}
		}
		else
		{
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
	}
	
	public Tuple next()
	{
		return iterator.getNext();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
