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
	String [] atts;
	Main main;
	
	public Project(Main main, RelationInfo R, String [] atts)
	{
		main = main;
		R = R;
		atts = atts;
	}
	
	public RelationInfo open()
	{
		// get the new type for new attributes
		String tempTableName = R.getName() + "_projected";
		
		String [][] newatts = new String[atts.length][4];
		Hashtable<String, Attribute> attHash = R.getAttributes();
		for (int i = 0; i < atts.length; i++)
		{
			Attribute att = attHash.get(atts[i]);
			newatts[i][0] = atts[i];
			newatts[i][1] = att.getType();
			newatts[i][2] = att.getLength();
			newatts[i][3] = att.getIsNullable();
		}
		// create a temporary relation or table
		main.createTable(main.getBm().getDBName(), tempTableName, newatts, true);
		
		/**
		 * use TableScan to scan tuple by tuple. Then form the values to be inserted
		 */
		
		IteratorInterface tempIterator = new TableScan(main, R);
		String [] attNames = Utility.getAttributeNames(R.getAttribute()); 
		String [][] query = new String[2][newatts.length];
		// get position of newatts in attNames
		int [] pos = new int[newatts.length];
		for (int i = 0; i < newatts.length; i++)
		{
			for (int j = 0; j < attNames.length; j++)
			{
				if (newatts[i].equals(attNames[j])) pos[i] = j;
			}
		}
		
		for (int i = 0; i < atts.length; i++)
		{
			query[0][i] = newatts[i][0];
			Tuple tuple = tempIterator.next();
		
			int tupleLength = Utility.getTotalLength(R.getAttribute());
			if (tuple != null)
			{
				// get the values for projected attributes and put them in query[][]
			
				Block block = tuple.getBlock();
				int offset = tuple.getOffset();
				byte [] data = block.getTupleContent(offset, tupleLength);
				String [] results = Utility.convertTupleToArray(attHash, data);
				// extract the projected data and insert it into query. Then call insertQuery in main
				query[1][i] = results[pos[i]];
				// insert the projected tuple into new tempRelation
				main.insertQuery(tempTableName, query);
			}
			tuple = tempIterator.next();
		}
		Hashtable hashTemp = main.getSysCat().getTempRelation();
		return (RelationInfo)hashTemp.get(tempTableName);
		//RelationInfo newR = (RelationInfo)hashTemp.get(tempTableName);
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
