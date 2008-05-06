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
	String [] condition;
	
	public Join(Main main, RelationInfo R1, RelationInfo R2, String [] condition, boolean index, int passNumber)
	{
		this.main = main;
		this.R1 = R1;
		this.R2 = R2;
		this.index = index;
		this.passNumber = passNumber;
		this.condition = condition;
	}
	
	public RelationInfo open()
	{
		
		int pos1=0, pos2=0;
		String tempTableName = R1.getName() + "_join_" + R2.getName();
		Hashtable attHash1 = R1.getAttribute();
		String [] attNames1 = Utility.getAttributeNames(attHash1);
		int tupleSize1 = Utility.getTotalLength(R1.getAttribute());
		
		Hashtable attHash2 = R2.getAttribute();
		String [] attNames2 = Utility.getAttributeNames(attHash2);
		int tupleSize2 = Utility.getTotalLength(R2.getAttribute());
		
		// get position of condition in R1 and R2
		for (int i = 0; i < attNames1.length; i++)
		{
			if (condition[0].equals(attNames1[i]))
			{
				pos1 = i;
				break;
			}
		}
		for (int i = 0; i < attNames2.length; i++)
		{
			if (condition[0].equals(attNames2[i]))
			{
				pos2 = i;
				break;
			}
		}
		
		String [][] atts = new String[attNames1.length + attNames2.length][4];
		int i;
		
		for (i =0; i < attNames1.length; i++)
		{
			Attribute att = (Attribute)attHash1.get(attNames1[i]);
			atts[i][0] = R1.getName() + "." + attNames1[i];
			atts[i][0] = attNames1[i];
			atts[i][1] = att.getType();
			atts[i][2] = att.getLength();
			atts[i][3] = att.getIsNullable();
		}
		for (int j = 0; j < attNames2.length; j++)
		{
			Attribute att = (Attribute)attHash2.get(attNames2[j]);
			atts[i+j][0] = R2.getName() + "." + attNames2[j];
			atts[i+j][1] = att.getType();
			atts[i+j][2] = att.getLength();
			atts[i+j][3] = att.getIsNullable();
		}
		// create a temporary relation
		main.createTable(tempTableName, atts, true);
		
		//IteratorInterface iterator1 = new TableScan(main, R1);
		System.out.println("R1 name " + R1.getName());
		Iterator iterator1 = new Iterator(main.getBm(), R1, Integer.parseInt(R1.getNumDataBlocks())); 
		for (int j = 0; j < Integer.parseInt(R1.getNumTuples().trim()); j++)
		{
			Tuple tuple1 = iterator1.getNext();
			Block block1 = tuple1.getBlock();
			//System.out.println("block1"+ block1);
			int offset = tuple1.getOffset();
			byte [] content = block1.getTupleContent(offset, tupleSize1);
			String [] results1 = Utility.convertTupleToArray(attHash1, content);
			
			if (!index)
			{	
				// Scan table 2 one by one and append it to result1 
				// IteratorInterface iterator2 = new TableScan(main, R2);
				Iterator iterator2 = new Iterator(main.getBm(), R2, Integer.parseInt(R2.getNumDataBlocks())); 
				for (int k = 0; k < Integer.parseInt(R2.getNumTuples().trim()); k++)
				{
					Tuple tuple2 = iterator2.getNext();
					Block block2 = tuple2.getBlock();
					offset = tuple2.getOffset();
					content = block2.getTupleContent(offset, tupleSize2);
					String [] results2 = Utility.convertTupleToArray(attHash2, content);
					
					// TODO need verify condition here
					if (condition[2].equals(">"))
					{
						if (Integer.parseInt(results1[pos1]) > Integer.parseInt(results2[pos2]))
						{
							int l;					
							String [][] query = new String[2][attNames1.length + attNames2.length];
			 				for (l = 0; l < attNames1.length; l++)
							{
								query[0][l] = atts[l][0];
								query[1][l] = results1[l];
							}
			 				for (int m = 0; m < attNames2.length; m++)
							{
			 					query[0][l+m] = atts[l+m][0];
								query[1][l+m] = results2[m];
							}
							main.insertQuery(tempTableName, query);
						}
					}
					if (condition[2].equals("<"))
					{
						if (Integer.parseInt(results1[pos1]) < Integer.parseInt(results2[pos2]))
						{
							int l;					
							String [][] query = new String[2][attNames1.length + attNames2.length];
			 				for (l = 0; l < attNames1.length; l++)
							{
								query[0][l] = atts[l][0];
								query[1][l] = results1[l];
							}
			 				for (int m = 0; m < attNames2.length; m++)
							{
			 					query[0][l+m] = atts[l+m][0];
								query[1][l+m] = results2[m];
							}
							main.insertQuery(tempTableName, query);
						}
					}
					if (condition[2].equals("="))
					{
						if (results1[pos1].equals(results2[pos2]))
						{
							int l;					
							String [][] query = new String[2][attNames1.length + attNames2.length];
			 				for (l = 0; l < attNames1.length; l++)
							{
								query[0][l] = atts[l][0];
								query[1][l] = results1[l];
							}
			 				for (int m = 0; m < attNames2.length; m++)
							{
			 					query[0][l+m] = atts[l+m][0];
								query[1][l+m] = results2[m];
							}
							main.insertQuery(tempTableName, query);
						}
					}
				}
			}
			else
			{
				// get offset of block in R2
				// iterate through the block and verify condition
			}
		}
		
		Hashtable hashTemp = main.getSysCat().getTempRelation();
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
