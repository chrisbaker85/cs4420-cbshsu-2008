import java.util.Hashtable;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.Set;
//import java.util.Iterator;
import java.util.Map;

public class IndexScan implements IteratorInterface {

	/**
	 * @param args
	 */
	
	Iterator iterator;
	RelationInfo R;
	Main main;
	String [] condition;
	
	public IndexScan(Main main, RelationInfo R, String [] condition)
	{
		if (R == null)
		{
			System.out.println("R is null");
			System.exit(0);
		}
		this.main = main;
		this.R = R;
		this.condition = condition;
	}
 	public RelationInfo open()
	{
 		String tempTableName = R.getName() + "_indexscaned";
 		System.out.println("Table name in index scan " + tempTableName);
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
		//RelationInfo relInfo = (RelationInfo)main.getSysCat().getTempRelation().get(tempTableName);
		
		// get index position
		int indexPos = -1;
		// find the postion of index in atttribute array
		for (int i = 0; i < attNames.length; i++)
		{
			if (attNames[i].equals(condition[0]))
			{
				indexPos = i;
				break;
			}
		}
		
		// get index info
		IndexInfo indexInfo = (IndexInfo)R.getIndexInfos().get(condition[0]);
		
		this.main.createIndexQuery(indexInfo.getIdexName(), tempTableName, condition[0], indexInfo.getIsDuplicate());
		TreeMap index = indexInfo.getIndex();
		
		System.out.println("size of index in tree map " + index.size());
		
		// check the type of operation ( >, < or =)
		if (condition[2].equals(">"))
		{
			// get the sorted key larder than specified value 
			SortedMap sortedmap = index.tailMap(condition[1]);
			
			System.out.println("In < and value is " + condition[1]);
			
			/**
			 * 1. get the offsets
			 * 2. get the tuple using the offset
			 * 3. insert tuple into tempRelation using main.insertQuery()
			 */
			// TODO: fix treemap and set below
			TreeMap tempTree = new TreeMap(sortedmap);
			
			Set s = sortedmap.keySet();
			java.util.Iterator i = s.iterator();
			while(i.hasNext())
			{
				Map.Entry me = (Map.Entry)i.next();
				//System.out.print(me.getKey() + ": ");
				//System.out.println(me.getValue());
			
				// get first key and value
				int key = (Integer)me.getKey();
				ArrayList<Integer> offsets = (ArrayList)me.getValue(); // offset in reference to the table
				
				for (int j = 0; j < offsets.size(); j++)
				{
					Block currentBlock = main.getBm().getBlock(Utility.combine(R.getId(), offsets.get(j)));
					int tupleOffset = 3;
					
					for (int k = 0; k < currentBlock.getRecordNumber(); k++)
					{
						byte [] data = currentBlock.getTupleContent(tupleOffset, tupleSize);
						String [] results = Utility.convertTupleToArray(attHash, data);
						if (Integer.parseInt(results[indexPos]) > Integer.parseInt(condition[1])) 
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
		if (condition[1].equals("="))
		{
			/**
			 * 1. get the offset using TreeMap.get()
			 * 2. convert it to tuple 
			 * 3. insert tuple into tempRelation using main.insertQuery()
			 */
			ArrayList<Integer> offsets = (ArrayList)index.get(condition[1]); // offset in reference to the table
			
			//RelationInfo relInfo = (RelationInfo)main.getSysCat().getTempRelation().get(tempTableName);
			
			for (int j = 0; j < offsets.size(); j++)
			{
				Block currentBlock = main.getBm().getBlock(Utility.combine(R.getId(), offsets.get(j)));
				int tupleOffset = 3;
				
				for (int k = 0; k < currentBlock.getRecordNumber(); k++)
				{
					byte [] data = currentBlock.getTupleContent(tupleOffset, tupleSize);
					String [] results = Utility.convertTupleToArray(attHash, data);
					if (Integer.parseInt(results[indexPos]) == Integer.parseInt(condition[1])) 
					{
						// insert the tuple into temporary table 
						main.insertQuery(tempTableName, Utility.formInsertQuery(attNames, results));
						break;
					}
					tupleOffset += tupleSize;
				}
			}
		}
		if (condition[1].equals("<"))
		{
			/**
			 * 1. get the offsets
			 * 2. get the tuple using the offset
			 * 3. insert tuple into tempRelation using main.insertQuery()
			 */
			
			// get the sorted key larder than specified value 
			SortedMap sortedmap = index.headMap(condition[1]);
			
			//TreeMap tempTree = new TreeMap(sortedmap);
			
			Set s = sortedmap.entrySet();
			java.util.Iterator i = s.iterator();
			while(i.hasNext())
			{
				Map.Entry me = (Map.Entry)i.next();
				//System.out.print(me.getKey() + ": ");
				//System.out.println(me.getValue());
			
				// get first key and value
				int key = (Integer)me.getKey();
				ArrayList<Integer> offsets = (ArrayList)me.getValue(); // offset in reference to the table
				
				//RelationInfo relInfo = (RelationInfo)main.getSysCat().getTempRelation().get(tempTableName);
				
				for (int j = 0; j < offsets.size(); j++)
				{
					Block currentBlock = main.getBm().getBlock(Utility.combine(R.getId(), offsets.get(j)));
					int tupleOffset = 3;
					
					for (int k = 0; k < currentBlock.getRecordNumber(); k++)
					{
						byte [] data = currentBlock.getTupleContent(tupleOffset, tupleSize);
						String [] results = Utility.convertTupleToArray(attHash, data);
						if (Integer.parseInt(results[indexPos]) < Integer.parseInt(condition[1])) 
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
		Tuple tuple = new Tuple();
		return tuple;
		// return iterator.getNext();
	}
	
	
}
