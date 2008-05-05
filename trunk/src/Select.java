import java.util.*;

/**
 * @author Sovandy Hang
 *
 */

public class Select implements IteratorInterface {

	/**
	 * @param args
	 */
	
	Iterator iterator;
	RelationInfo R;
	String [] condition;
	Main main;
	boolean index;
	
	public Select(Main main, RelationInfo R, String [] condition, boolean index)
	{
		this.main = main;
		this.R = R;
		this.condition = condition;
		this.index = index;
		/*
		System.out.println("--------------------------------------");
		System.out.println("Relation name in select " + R.getName());
		System.out.println("Field name is " + condition[0]);
		System.out.println("Number of tuple " + R.getNumTuples());
		System.out.println("--------------------------------------");
		*/
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
		main.createTable(tempTableName, atts, true);
		// if index is not specified
		if (!index) 
		{
			//System.out.println("In case there is no index");
			// get the position of condition in the array of attributes
			int ind;
			for (ind = 0; ind < attNames.length; ind++)
			{
				if (attNames[ind].equals(Utility.getField(condition[0]))) 
				{
					System.out.println("Field name found " + Utility.getField(condition[0]));
					break;
				}
			}
			if (ind == attNames.length) 
			{
				System.out.println("Couldn't find " + Utility.getField(condition[0]));
			}
			
			System.out.println("Match fields " + Utility.getField(condition[0]) + " " + attNames[ind]);
			// use tablescan to interate through relation
			IteratorInterface iterator = new TableScan(main, R);
			iterator.open();
			Tuple tuple;
			
			String [][] query = new String[2][attNames.length];
			for (int i = 0; i < Integer.parseInt(R.getNumTuples().trim()); i++)
			{
				tuple = iterator.next();
				Block block = tuple.getBlock();
				int offset = tuple.getOffset();
				byte [] content = block.getTupleContent(offset, tupleSize);
				String [] results = Utility.convertTupleToArray(attHash, content);
				
				// compare it result with condition
				if (condition[2].equals(">"))
				{
					System.out.println("Verifying >");
					if (Integer.parseInt(results[ind]) > Integer.parseInt(condition[1]))
					{
						System.out.println("Match :" + Integer.parseInt(results[ind]) + " > " + Integer.parseInt(condition[1]));
						// form query to be inserted
						query = Utility.formInsertQuery(attNames, results);
						// insert the tuple here by calling main.insertQuery()
						main.insertQuery(tempTableName, query);
					}
				}
				if (condition[2].equals("="))
				{
					//System.out.println("Verifying =");
					// check type to see if it's int or string. Then, compare the two
					Attribute a = (Attribute)attHash.get(attNames[ind]);
					// if it's integer
					if(a.getType().equals("int"))
					{
						System.out.println("Select without index and in INT type");
						if (Integer.parseInt(results[ind]) == Integer.parseInt(condition[1]))
						{
							// System.out.println("Match :" + Integer.parseInt(results[ind]) + " = " + Integer.parseInt(condition[1]));
							// form query to be inserted
							query = Utility.formInsertQuery(attNames, results);
							// insert the tuple here by calling main.insertQuery()
							main.insertQuery(tempTableName, query);
						}
					}
					else
					{
						System.out.println("Select without index and in STRING type");
						if (results[ind].startsWith(condition[1]))
						{
							//System.out.println("Match :" + Integer.parseInt(results[ind]) + " = " + Integer.parseInt(condition[1]));
							// form query to be inserted
							query = Utility.formInsertQuery(attNames, results);
							// insert the tuple here by calling main.insertQuery()
							main.insertQuery(tempTableName, query);
						}
					}
				}
				if (condition[2].equals("<"))
				{
					//System.out.println("Verifying <");
					if (Integer.parseInt(results[ind]) < Integer.parseInt(condition[1]))
					{
						System.out.println("Match :" + Integer.parseInt(results[ind]) + " < " + Integer.parseInt(condition[1]));
						// form query to be inserted
						query = Utility.formInsertQuery(attNames, results);
						// insert the tuple here by calling main.insertQuery()
						main.insertQuery(tempTableName, query);
					}
				}
			}
		}
		// if there is index
		else
		{
			IndexScan indexscan = new IndexScan(this.main, R, condition);
			return indexscan.open();
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
