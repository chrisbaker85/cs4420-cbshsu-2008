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
	}
	
	public RelationInfo open()
	{
		// get the new type for new attributes
		String tempTableName = R.getName() + "_projected";
		
		String [][] newatts = new String[attList.length][4];
		Hashtable<String, Attribute> attHash = R.getAttributes();
		for (int i = 0; i < attList.length; i++)
		{
			Attribute att = attHash.get(Utility.getField(attList[i]));
			newatts[i][0] = Utility.getField(attList[i]);
			newatts[i][1] = att.getType();
			newatts[i][2] = att.getLength();
			newatts[i][3] = att.getIsNullable();
		}
		// create a temporary relation or table
		main.createTable(tempTableName, newatts, true);
		
		/**
		 * use TableScan to scan tuple by tuple. Then form the values to be inserted
		 */
		
		String [] attNames = Utility.getAttributeNames(R.getAttribute()); 
		String [][] query = new String[2][newatts.length];
		// get position of newatts in attNames
		int [] attPos = new int[newatts.length];
		// find the position of new attribute in original attributes  
		for (int i = 0; i < newatts.length; i++)
		{
			// search for position of new attribute one by one in original attribute
			for (int j = 0; j < attNames.length; j++)
			{
				if (Utility.getField(newatts[0][i]).equals(attNames[j])) 
				{
					attPos[i] = j;
					// check to see if that field is an index in original table
					if (R.getIndexInfos().containsKey(attNames[j]))
					{
						IndexInfo indexInfo = (IndexInfo)R.getIndexInfos().get(attNames[j]);
						this.main.createIndexQuery(indexInfo.getIdexName(), tempTableName, attNames[j], indexInfo.getIsDuplicate());
					}
				}
			}
		}
		
		Iterator tempIterator = new Iterator(main.getBm(), R, Integer.parseInt(R.getNumDataBlocks()));
		tempIterator.open();
		
		int tupleSize = Utility.getTotalLength(R.getAttribute());
		
		System.out.println("Relation name " + R.getName());
		System.out.println("The number of of tuple " + R.getNumTuples());
		
		for (int i = 0; i < Integer.parseInt(R.getNumTuples()); i++)
		{
			Tuple tuple = tempIterator.getNext();
			if (tuple != null)
			{
				// get the values for projected attributes and put them in query[][]
			
				Block block = tuple.getBlock();
				int offset = tuple.getOffset();
				byte [] data = block.getTupleContent(offset, tupleSize);
				String [] results = Utility.convertTupleToArray(attHash, data);
		
				for(int j = 0; j < attList.length; j++)
				{
					query[0][j] = newatts[j][0];	
					// extract the projected data and insert it into query. Then call insertQuery in main
					query[1][j] = results[attPos[j]];
					System.out.println("Data : " + query[1][j]);
					// insert the projected tuple into new tempRelation
				}
				main.insertQuery(tempTableName, query);
				//tuple = tempIterator.getNext();
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
