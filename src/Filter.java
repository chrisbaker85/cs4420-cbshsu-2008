import java.util.*;

/**
 * @author Sovandy
 *
 */
public class Filter implements IteratorInterface {

	/**
	 * @param args
	 */
	
	Iterator iterator;
	Main main;
	RelationInfo R;
	String [][] where;
	
	public Filter(Main main, RelationInfo R, String [][] where)
	{
		this.main = main;
		this.R = R;
		this.where = where;
	}
	
	public RelationInfo open()
	{
		/**
		 * there are two way of doing it.
		 * 1. Scan tuple (using IndexScan or TableScan) tuple by tuple and compare with all conditions
		 * 2. Call Select multiple time if it's AND condition (con1 AND con2 AND con3...)
		 * tempRelation will be used to  
		 */ 
		
		String tempTableName = R.getName() + "_filtered";
		Hashtable attHash = R.getAttribute();
		String [] attNames = Utility.getAttributeNames(attHash);
		int tupleSize = Utility.getTotalLength(R.getAttribute());
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
		main.createTable(tempTableName, atts, true);
		
		
		// get the position of condition in the array of attributes
		int [] pos = new int[where.length];
		int ind = 0; 
		for (int i = 0; i < attNames.length; i++)
		{
			if (attNames[i].equals(where[0]))
			{
				pos[ind++] = i;
			}
		}
		// use tablescan to iterate through relation
		Iterator iterator = new Iterator(main.getBm(), R, Integer.parseInt(R.getNumDataBlocks())); 
		Tuple tuple;
		
		// TODO figure out the order of condition based on the number of distinct values, small should be done first?
		
		for (int i = 0; i < Integer.parseInt(R.getNumTuples().trim()); i++)
		{
			tuple = iterator.getNext();
			Block block = tuple.getBlock();
			int offset = tuple.getOffset();
			byte [] content = block.getTupleContent(offset, tupleSize);
			String [] results = Utility.convertTupleToArray(attHash, content);
			// compare it result with condition
			boolean allOp = true;
			for (int j = 0; j < where.length; j++)
			{
				if (where[j] != null && where[j][2].equals(">"))
				{
					if (Integer.parseInt(results[ind]) <= Integer.parseInt(where[j][2]))
					{
						allOp = false;
						break;
					}
				}
				if (where[j] != null && where[j][2].equals("="))
				{
					// test the attribute type
					Attribute att = (Attribute)attHash.get(where[j][0]);
					if (att.getType().equals("string"))
					{	
						if (!results[ind].startsWith(where[j][1]))
						{
							allOp = false;
							break;
						}
					}
					else 
					{
						if (Integer.parseInt(results[ind]) != Integer.parseInt(where[j][1]))
						{
							allOp = false;
							break;
						}
					}
				}
				if (where[j] != null && where[j][2].equals("<"))
				{
					if (Integer.parseInt(results[ind]) >= Integer.parseInt(where[j][2]))
					{
						allOp = false;
						break;
					}
				}
				if (allOp)
				{
					// insert tuple into tempTableName by calling main.insertQuery()
					main.insertQuery(tempTableName, Utility.formInsertQuery(attNames, results));
				}
			}
		}
		
		Hashtable hashTemp = main.getSysCat().getTempRelation();
		//RelationInfo newR = (RelationInfo)hashTemp.get(tempTableName);
		//iterator = new Iterator(main.getBm(), newR, newR.getId(), Integer.parseInt(newR.getNumDataBlocks()));
		return (RelationInfo)hashTemp.get(tempTableName);
	}
	
	public Tuple next()
	{
		return iterator.getNext();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
