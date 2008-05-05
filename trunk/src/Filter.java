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
	String [][] conditions;
	
	public Filter(Main main, RelationInfo R, String [][] conditions)
	{
		this.main = main;
		this.R = R;
		this.conditions = conditions;
	}
	
	public RelationInfo open()
	{
		/**
		 * there are two way of doing it.
		 * 1. Scan tuple (using IndexScan or TableScan) tuple by tuple and compare with all conditions
		 * 2. Call Select multiple time if it's AND condition (con1 AND con2 AND con3...)
		 * tempRelation will be used to  
		 */ 
		
		//System.out.println("Operator is " + conditions[0][2]);
		//System.out.println("Operator is " + conditions[1][2]);
		
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
		int [] pos = new int[conditions.length];
		int ind = 0; 
		for (int i = 0; i < conditions.length; i++)
		{
			for (int j = 0; j < attNames.length; j++)
			{
				if (attNames[j].equals(conditions[i][0]))
				{
					pos[ind++] = j;
				}
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
			//System.out.println("Length of conditions : " + conditions.length);
			for (int j = 0; j < conditions.length; j++)
			{
				
				//System.out.println("Operator is " + conditions[j][2] + " and j is " + j);
				
				if (conditions[j] != null && conditions[j][2].equals(">"))
				{
					//System.out.println("In > condition");
					//System.out.println("comparing " + results[pos[j]] + " > " + conditions[j][1]);
					System.out.println("comparing " + results[pos[j]] + " > " + conditions[j][1]);
					if (Integer.parseInt(results[pos[j]]) <= Integer.parseInt(conditions[j][1]))
					{
						allOp = false;
						break;
					}
				}
				else if (conditions[j] != null && conditions[j][2].equals("="))
				{
					// test the attribute type
					Attribute att = (Attribute)attHash.get(conditions[j][0]);
					if (att.getType().equals("string"))
					{	
						//System.out.println("In = condition in STRING type");
						if (!results[pos[j]].startsWith(conditions[j][1]))
						{
							allOp = false;
							break;
						}
					}
					else 
					{
						//System.out.println("In = condition in INT type");
						if (Integer.parseInt(results[pos[j]]) != Integer.parseInt(conditions[j][1]))
						{
							allOp = false;
							break;
						}
					}
				}
				else if (conditions[j] != null && conditions[j][2].equals("<"))
				{
					//System.out.println("In < condition");
					System.out.println("comparing " + results[pos[j]] + " < " + conditions[j][1]);
					if (Integer.parseInt(results[pos[j]]) >= Integer.parseInt(conditions[j][1]))
					{
						allOp = false;
						break;
					}
				}
			}
			if (allOp)
			{
				// insert tuple into tempTableName by calling main.insertQuery()
				main.insertQuery(tempTableName, Utility.formInsertQuery(attNames, results));
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
