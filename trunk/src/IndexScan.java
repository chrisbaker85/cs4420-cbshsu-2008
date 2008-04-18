import java.util.Hashtable;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.ArrayList;


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
		int indexPos = -1;
		// find the postion of index in atttribute array
		for (int i = 0; i < attNames.length; i++)
		{
			if (attNames[i].equals(where[0]))
			{
				indexPos = i;
				break;
			}
		}
		
		// get index info
		IndexInfo indexInfo = (IndexInfo)R.getIndexInfos().get(where[0]);
		
		this.main.createIndexQuery(indexInfo.getIdexName(), tempTableName, where[0], indexInfo.getIsDuplicate());
		
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
				// get first key and value
				int firstkey = (Integer)tempTree.firstKey();
				ArrayList<Integer> offsets = (ArrayList)index.get(firstkey); // offset in reference to the table
				tempTree.remove(firstkey);
				
				RelationInfo relInfo = (RelationInfo)main.getSysCat().getTempRelation().get(tempTableName);
				
				for (int j = 0; j < offsets.size(); j++)
				{
					Block currentBlock = main.getBm().getBlock(Utility.combine(relInfo.getId(), offsets.get(j)));
					int tupleOffset = 3;
					
					for (int k = 0; k < currentBlock.getRecordNumber(); k++)
					{
						byte [] data = currentBlock.getTupleContent(tupleOffset, tupleSize);
						String [] results = Utility.convertTupleToArray(attHash, data);
						if (Integer.parseInt(results[indexPos]) > Integer.parseInt(where[1])) 
						{
							// insert the tuple into temporary table 
							main.insertQuery(tempTableName, Utility.formInsertQuery(attNames, results));
							break;
						}
						tupleOffset += tupleSize;
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
			ArrayList<Integer> offsets = (ArrayList)index.get(where[1]); // offset in reference to the table
			
			RelationInfo relInfo = (RelationInfo)main.getSysCat().getTempRelation().get(tempTableName);
			
			for (int j = 0; j < offsets.size(); j++)
			{
				Block currentBlock = main.getBm().getBlock(Utility.combine(relInfo.getId(), offsets.get(j)));
				int tupleOffset = 3;
				
				for (int k = 0; k < currentBlock.getRecordNumber(); k++)
				{
					byte [] data = currentBlock.getTupleContent(tupleOffset, tupleSize);
					String [] results = Utility.convertTupleToArray(attHash, data);
					if (Integer.parseInt(results[indexPos]) == Integer.parseInt(where[1])) 
					{
						// insert the tuple into temporary table 
						main.insertQuery(tempTableName, Utility.formInsertQuery(attNames, results));
						break;
					}
					tupleOffset += tupleSize;
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
				// get first key and value
				int firstkey = (Integer)tempTree.firstKey();
				ArrayList<Integer> offsets = (ArrayList)index.get(firstkey); // offset in reference to the table
				tempTree.remove(firstkey);
				
				RelationInfo relInfo = (RelationInfo)main.getSysCat().getTempRelation().get(tempTableName);
				
				for (int j = 0; j < offsets.size(); j++)
				{
					Block currentBlock = main.getBm().getBlock(Utility.combine(relInfo.getId(), offsets.get(j)));
					int tupleOffset = 3;
					
					for (int k = 0; k < currentBlock.getRecordNumber(); k++)
					{
						byte [] data = currentBlock.getTupleContent(tupleOffset, tupleSize);
						String [] results = Utility.convertTupleToArray(attHash, data);
						if (Integer.parseInt(results[indexPos]) < Integer.parseInt(where[1])) 
						{
							// insert the tuple into temporary table 
							main.insertQuery(tempTableName, Utility.formInsertQuery(attNames, results));
							break;
						}
						tupleOffset += tupleSize;
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
