import java.util.Hashtable;

/**
 * @author Sovandy Hang
 *
 */

public class Project implements IteratorInterface {

	/**
	 * @param args
	 */
	
	Iterator iterator;
	RelationInfo R;
	String [] attList;
	Main main;
	
	public Project(Main main, RelationInfo R, String [] attList)
	{
		this.main = main;
		this.R = R;
		this.attList = attList;
		System.out.println("Relation id " + R.getId());
		System.out.println("--------------------------------------");
		System.out.println("Relation name in project " + R.getName());
		System.out.println("Number of tuple " + R.getNumTuples());
		/*
		for (int i = 0; i < attList.length; i++)
		{
			System.out.print(attList[i] + " ");
		}
		System.out.println();
		System.out.println("--------------------------------------");
		
		Iterator iterator = new Iterator(this.main.getBm(), R, Integer.parseInt(R.getNumDataBlocks().trim()));
		Hashtable atts = R.getAttributes();
		int tupleSize = Utility.getTotalLength(atts);
		Tuple tuple;
		String [] attNames = Utility.getAttributeNames(atts);
		for (int j = 0; j < attNames.length; j++)
		{
			System.out.print(attNames[j] + "\t");
		}
		System.out.println("");
		System.out.println("==============================================");
		
		for (int i = 0; i < Integer.parseInt(R.getNumTuples().trim()); i++)
		{
			if (Debug.get().debug()) System.out.println("INFO: (main) index " + i);
			tuple = iterator.getNext();
			Block block = tuple.getBlock();
			int offset = tuple.getOffset();
			byte [] data = block.getTupleContent(offset, tupleSize);
			String [] results = Utility.convertTupleToArray(atts, data);
			for (int j = 0; j < attNames.length; j++)
			{
				System.out.print(results[j] + "\t");
			}
			System.out.println("");
		}
		*/
	}
	
	public RelationInfo open()
	{
		String tempTableName = R.getName() + "_projected";
		Hashtable attHash = R.getAttribute();
		//String [] attNames = Utility.getAttributeNames(attHash);
		String [][] atts = new String[attList.length][4];
		int tupleSize = Utility.getTotalLength(R.getAttribute());
		for (int i = 0; i < attList.length; i++)
		{
			Attribute att = (Attribute)attHash.get(attList[i]);
			atts[i][0] = attList[i];
			atts[i][1] = att.getType();
			atts[i][2] = att.getLength();
			atts[i][3] = att.getIsNullable();
		}
		// create a temporary relation
		main.createTable(tempTableName, atts, true);
		// if index is not specified
		
		/*
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
		*/
		
		Hashtable hashTemp = main.getSysCat().getTempRelation();
		RelationInfo newR = (RelationInfo)hashTemp.get(tempTableName);
		Hashtable newAttHash = newR.getAttribute();
		
		String [] newAttNames = Utility.getAttributeNames(newAttHash);
		
		//System.out.println("ID in project " + ((RelationInfo)hashTemp.get(tempTableName)).getId());
		
		// System.out.println("Match fields " + Utility.getField(condition[0]) + " " + attNames[ind]);
		// use tablescan to interate through relation
		IteratorInterface iterator = new TableScan(main, R);
		iterator.open();
		Tuple tuple;
		
		String [][] query = new String[2][attList.length];
		for (int i = 0; i < Integer.parseInt(R.getNumTuples().trim()); i++)
		{
			tuple = iterator.next();
			Block block = tuple.getBlock();
			int offset = tuple.getOffset();
			byte [] content = block.getTupleContent(offset, tupleSize);
			String [] results = Utility.convertTupleToArray(attHash, content);
			
			query = Utility.formInsertQuery(newAttNames, results);
			// insert the tuple here by calling main.insertQuery()
			main.insertQuery(tempTableName, query);
		}
		
		//RelationInfo newR = (RelationInfo)hashTemp.get(tempTableName);
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
