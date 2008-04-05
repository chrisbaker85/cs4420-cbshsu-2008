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
		main = main;
		R = R;
		where = where;
	}
	
	public void open()
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
		main.createTable(main.getBm().getDBName(), tempTableName, atts, true);
		
		// find out which condition is indexed
		
		int indexPos = -1;
		for (int i = 0; i < where.length; i++)
		{
			if (R.getColsIndexed().equals(where[i]))
			{
				indexPos = i;
				break;
			}
		}
		// set index exists false right now
		indexPos = -1;
		if (indexPos == -1)
		{
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
			IteratorInterface iterator = new TableScan(main, R);
			Tuple tuple;
			
			
			for (int i = 0; i < Integer.parseInt(R.getNumTuples().trim()); i++)
			{
				tuple = iterator.next();
				Block block = tuple.getBlock();
				int offset = tuple.getOffset();
				byte [] content = block.getTupleContent(offset, tupleSize);
				String [] results = Utility.convertTupleToArray(attHash, content);
				// compare it result with condition
				boolean allOp = true;
				for (int j = 0; j < where.length; j++)
				{
					if (where[1].equals(">"))
					{
						if (Integer.parseInt(results[ind]) <= Integer.parseInt(where[j][2]))
						{
							allOp = false;
							break;
						}
					}
					if (where[1].equals("="))
					{
						if (Integer.parseInt(results[ind]) != Integer.parseInt(where[j][2]))
						{
							allOp = false;
							break;
						}	
					}
					if (where[1].equals("<"))
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
		}
		else
		{
			/**
			 * 1. get all the keys that meet the condition of that index
			 * 2. in each tuple that meet the condition, compare with the rest of conditions
			 * 3. insert the tuple that meet all the condition 
			 */
			
			TreeMap index = R.getIndexInfo().getIndex();
			if (where[1].equals(">"))
			{
				// get the sorted key larder than specified value 
				SortedMap sortedmap = index.tailMap(where[2]);
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
						if (results[indexPos].equals(where[2])) 
						{
							// TODO: compare with other conditions
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
				Integer offset = (Integer)index.get(where[2]);
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
						if (results[indexPos].equals(where[2])) 
						{
							// TODO: compare with other conditions
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
				SortedMap sortedmap = index.headMap(where[2]);
				
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
						if (results[indexPos].equals(where[2])) 
						{
							// TODO: compare with other condition
							// insert the tuple into temporary table 
							main.insertQuery(tempTableName, Utility.formInsertQuery(attNames, results));
							break;
						}
					}
				}
			}
		}
		Hashtable hashTemp = main.getSysCat().getTempRelation();
		RelationInfo newR = (RelationInfo)hashTemp.get(tempTableName);
		iterator = new Iterator(main.getBm(), newR, newR.getId(), Integer.parseInt(newR.getNumDataBlocks()));
		
	}
	
	public Tuple next()
	{
		return iterator.getNext();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
