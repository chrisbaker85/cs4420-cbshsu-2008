import java.util.Hashtable;
import java.util.SortedMap;
import java.util.TreeMap;


public class IndexScan implements IteratorInterface {

	/**
	 * @param args
	 */
	
	Iterator iterator;
	RelationInfo R;
	Main main;
	String [] where;
	
	public IndexScan(Main main, RelationInfo R, String [] where)
	{
		main = main;
		R = R;
		where = where;
	}
 	public RelationInfo open()
	{
 		String tempTableName = R.getName() + "_indexscaned";
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
		main.createTable(tempTableName, atts, true);
		
		// get index position
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
		
		// get index info
		IndexInfo indexInfo = (IndexInfo)R.getIndexInfos().get(where[0]);
		TreeMap index = indexInfo.getIndex();
		// check the type of operation ( >, < or =)
		if (where[2].equals(">"))
		{
			// get the sorted key larder than specified value 
			SortedMap sortedmap = index.tailMap(where[1]);
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
					if (results[indPos].equals(where[1])) 
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
			Integer offset = (Integer)index.get(where[1]);
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
					if (results[indPos].equals(where[1])) 
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
			SortedMap sortedmap = index.headMap(where[1]);
			
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
					if (results[indPos].equals(where[1])) 
					{
						// insert the tuple into temporary table 
						main.insertQuery(tempTableName, Utility.formInsertQuery(attNames, results));
						break;
					}
				}
			}
		}
		Hashtable hashTemp = main.getSysCat().getTempRelation();
		return (RelationInfo)hashTemp.get(tempTableName);
	}
	
	public Tuple next()
	{
		return iterator.getNext();
	}
	
}
