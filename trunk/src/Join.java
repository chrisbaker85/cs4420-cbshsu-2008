import java.util.*;

/**
 * @author Sovandy Hang
 *
 */
public class Join implements IteratorInterface{

	/**
	 * @param args
	 */
	Iterator iterator;
	Main main;
	RelationInfo R1;
	RelationInfo R2;
	boolean index;
	int passNumber;
	String [] conditions;
	
	public Join(Main main, RelationInfo R1, RelationInfo R2, String [] conditions, boolean index, int passNumber)
	{
		main = main;
		R1 = R1;
		R2 = R2;
		index = index;
		passNumber = passNumber;
	}
	public void open()
	{
		String tempTableName = R1.getColsIndexed() + "_JOIN_" + R2.getName();
		/**
		 * 1. get attributes of R1 and R2
		 * 2. compare and merge them
		 * 3. create table for that relation
		 * OPERATION
		 * ---------
		 * 1. In table 1, get one tuple at a time
		 * 2. select from table 2 with condition of field = value of table 1
		 * 3. insert into new table
		 */ 
		
		Hashtable attHash1 = R1.getAttribute();
		String [] attNames1 = Utility.getAttributeNames(attHash1);
		int tupleSize1 = Utility.getTotalLength(R1.getAttribute());
		
		Hashtable attHash2 = R2.getAttribute();
		String [] attNames2 = Utility.getAttributeNames(attHash2);
		int tupleSize2 = Utility.getTotalLength(R2.getAttribute());
		
		String [][] atts = new String[attNames1.length + attNames2.length][4];
		int i;
		for (i =0; i < attNames1.length; i++)
		{
			Attribute att = (Attribute)attHash1.get(attNames1[i]);
			atts[i][0] = attNames1[i];
			atts[i][1] = att.getType();
			atts[i][2] = att.getLength();
			atts[i][3] = att.getIsNullable();
		}
		for (int j = 0; j < attNames2.length; j++)
		{
			Attribute att = (Attribute)attHash2.get(attNames2[j]);
			atts[i+j][0] = attNames2[j];
			atts[i+j][1] = att.getType();
			atts[i+j][2] = att.getLength();
			atts[i+j][3] = att.getIsNullable();
		}
		// create a temporary relation
		main.createTable(main.getBm().getDBName(), tempTableName, atts, true);
		
		IteratorInterface iterator1 = new TableScan(main, R1);
		for (int j = 0; j < Integer.parseInt(R1.getNumTuples().trim()); j++)
		{
			Tuple tuple1 = iterator1.next();
			Block block1 = tuple1.getBlock();
			int offset = tuple1.getOffset();
			byte [] content = block1.getTupleContent(offset, tupleSize1);
			String [] results1 = Utility.convertTupleToArray(attHash1, content);
			
			// If there is no index for relation 2, scan table 2 one by one and append it to result1 
			IteratorInterface iterator2 = new TableScan(main, R2);
			for (int k = 0; k < Integer.parseInt(R2.getNumTuples().trim()); k++)
			{
				Tuple tuple2 = iterator2.next();
				Block block2 = tuple2.getBlock();
				offset = tuple2.getOffset();
				content = block2.getTupleContent(offset, tupleSize1);
				String [] results2 = Utility.convertTupleToArray(attHash2, content);
				
				// TODO need to verify condition here before writing tuple. If found, write to new relation and break out of loop
				// combine result 
				int l;
				String [][] query = new String[2][attNames2.length + attNames2.length];
 				for (l = 0; l < attNames1.length; l++)
				{
					query[l][0] = attNames1[l];
					query[l][1] = results1[l];
				}
 				for (int m = 0; m < attNames2.length; m++)
				{
 					query[l+m][0] = attNames2[l];
					query[l+m][1] = results2[l];
				}
				main.insertQuery(tempTableName, query);
			}
			
			// TODO if there is index, get the key in relation 1, and search for value in relation 2 index
			// If found, write it to new relation
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
