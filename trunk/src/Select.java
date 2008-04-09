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
	RelationInfo R;
	String [][] where;
	Main main;
	boolean index;
	
	public Select(Main main, RelationInfo R, String [][] where, boolean index)
	{
		main = main;
		R = R;
		where = where;
		index = index;
	}
	
	// public void open(RelationInfo R, String [] where, Index idx)
	public RelationInfo open()
	{
		String tempTableName = R.getName() + "_selected";
		Hashtable attHash = R.getAttribute();
		String [] attNames = Utility.getAttributeNames(attHash);
		String [][] atts = new String[attNames.length][4];
		int tupleSize = Utility.getTotalLength(R.getAttribute());
		for (int i = 0; i < attNames.length; i++)
		{
			Attribute att = (Attribute)attHash.get(attNames[i]);
			atts[i][0] = attNames[i];
			atts[i][1] = att.getType();
			atts[i][2] = att.getLength();
			atts[i][3] = att.getIsNullable();
		}
		// create a temporary relation
		main.createTable(main.getBm().getDBName(), tempTableName, atts, true);
		// if index is not specified
		if (!index) 
		{
			// get the position of condition in the array of attributes
			int ind;
			for (ind = 0; ind < attNames.length; ind++)
			{
				if (attNames[ind].equals(where[0][0])) break;
			}
			// use tablescan to interate through relation
			IteratorInterface iterator = new TableScan(main, R);
			Tuple tuple;
			
			String [][] query = new String[2][attNames.length];
			for (int i = 0; i < Integer.parseInt(R.getNumTuples().trim()); i++)
			{
				tuple = iterator.next();
				Block block = tuple.getBlock();
				int offset = tuple.getOffset();
				byte [] content = block.getTupleContent(offset, tupleSize);
				String [] results = Utility.convertTupleToArray(attHash, content);
				// form query to be inserted
				query = Utility.formInsertQuery(attNames, results);
				
				// compare it result with condition
				if (where[0][2].equals(">"))
				{
					if (Integer.parseInt(results[ind]) > Integer.parseInt(where[0][1]))
					{
						// insert the tuple here by calling main.insertQuery()
						main.insertQuery(tempTableName, query);
					}
				}
				if (where[1].equals("="))
				{
					if (Integer.parseInt(results[ind]) == Integer.parseInt(where[0][1]))
					{
						// insert the tuple here by calling main.insertQuery()
						main.insertQuery(tempTableName, query);
					}
				}
				if (where[0][2].equals("<"))
				{
					if (Integer.parseInt(results[ind]) < Integer.parseInt(where[0][1]))
					{
						// insert the tuple here by calling main.insertQuery()
						main.insertQuery(tempTableName, query);
					}
				}
			}
		}
		// if index is not specified
		else
		{
			// get index information
			TreeMap index = R.getIndexInfo().getIndex();
			int indPos = -1;
			// find the postion of index in atttribute array
			for (int i = 0; i < attNames.length; i++)
			{
				if (attNames[i].equals(R.getColsIndexed()))
				{
					indPos = i;
					break;
				}
			}
			// check the type of operation ( >, < or =)
			if (where[0][2].equals(">"))
			{
				// get the sorted key larder than specified value 
				SortedMap sortedmap = index.tailMap(where[0][1]);
				/**
				 * 1. get the offsets
				 * 2. get the tuple using the offset
				 * 3. insert tuple into tempRelation using main.insertQuery()
				 */
				
				TreeMap tempTree = new TreeMap(sortedmap);
				
				for (int i = 0; i < tempTree.size(); i++)
				{
					int firstkey = (Integer)tempTree.firstKey();
					int offset = (Integer)index.get(firstkey); // offset in reference to the table
					tempTree.remove(firstkey);
					int tupleNumPerBlock = Parameters.BLOCK_SIZE / tupleSize;
					RelationInfo relInfo = (RelationInfo)main.getSysCat().getTempRelation().get(tempTableName);
					// get the block that the tuple is in
					Block current_block = main.getBm().getBlock(Utility.combine(relInfo.getId(), offset));
					int tupleOffset = 3;
					for (int j = 0; j < tupleNumPerBlock; j++)
					{
						byte [] data = current_block.getTupleContent(offset, tupleSize);
						String [] results = Utility.convertTupleToArray(attHash, data);
						if (results[indPos].equals(where[0][1])) 
						{
							// insert the tuple into temporary table 
							main.insertQuery(tempTableName, Utility.formInsertQuery(attNames, results));
							break;
						}
					}
				}
			} 
			if (where[1].equals("="))
			{
				/**
				 * 1. get the offset using TreeMap.get()
				 * 2. convert it to tuple 
				 * 3. insert tuple into tempRelation using main.insertQuery()
				 */
				Integer offset = (Integer)index.get(where[0][1]);
				if (offset != null)
				{
					// scan through block to find the tuple
					int tupleNumPerBlock = Parameters.BLOCK_SIZE / tupleSize;
					RelationInfo relInfo = (RelationInfo)main.getSysCat().getTempRelation().get(tempTableName);
					// get the block that the tuple is in
					Block current_block = main.getBm().getBlock(Utility.combine(relInfo.getId(), offset));
					int tupleOffset = 3;
					for (int j = 0; j < tupleNumPerBlock; j++)
					{
						byte [] data = current_block.getTupleContent(offset, tupleSize);
						String [] results = Utility.convertTupleToArray(attHash, data);
						if (results[indPos].equals(where[0][1])) 
						{
							// insert the tuple into temporary table 
							main.insertQuery(tempTableName, Utility.formInsertQuery(attNames, results));
							break;
						}
					}
				}
			}
			if (where[1].equals("<"))
			{
				/**
				 * 1. get the offsets
				 * 2. get the tuple using the offset
				 * 3. insert tuple into tempRelation using main.insertQuery()
				 */
				
				// get the sorted key larder than specified value 
				SortedMap sortedmap = index.headMap(where[0][1]);
				
				TreeMap tempTree = new TreeMap(sortedmap);
				for (int i = 0; i < tempTree.size(); i++)
				{
					int firstkey = (Integer)tempTree.firstKey();
					int offset = (Integer)index.get(firstkey); // offset in reference to the table
					tempTree.remove(firstkey);
					int tupleNumPerBlock = Parameters.BLOCK_SIZE / tupleSize;
					RelationInfo relInfo = (RelationInfo)main.getSysCat().getTempRelation().get(tempTableName);
					// get the block that the tuple is in
					Block current_block = main.getBm().getBlock(Utility.combine(relInfo.getId(), offset));
					int tupleOffset = 3;
					for (int j = 0; j < tupleNumPerBlock; j++)
					{
						byte [] data = current_block.getTupleContent(offset, tupleSize);
						String [] results = Utility.convertTupleToArray(attHash, data);
						if (results[indPos].equals(where[0][1])) 
						{
							// insert the tuple into temporary table 
							main.insertQuery(tempTableName, Utility.formInsertQuery(attNames, results));
							break;
						}
					}
				}
			}
		}
		Hashtable hashTemp = main.getSysCat().getTempRelation();
		//RelationInfo newR = (RelationInfo)hashTemp.get(tempTableName);
		return (RelationInfo)hashTemp.get(tempTableName);
		//iterator = new Iterator(main.getBm(), newR, newR.getId(), Integer.parseInt(newR.getNumDataBlocks()));
	}
	
	public Tuple next()
	{
		return iterator.getNext();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
