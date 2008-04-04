import java.util.Hashtable;

/**
 * @author Sovandy
 *
 */
public class Project implements IteratorInterface {

	/**
	 * @param args
	 */
	
	Iterator iterator;
	BufferManager bm;
	RelationInfo R;
	String [] atts;
	
	public Project(Main main, BufferManager bm, RelationInfo R, String [] atts)
	{
		main = main;
		bm = bm;
		R = R;
		atts = atts;
	}
	
	public void open()
	{
		// get the new type for new attributes
		String tempRelation;
		String [][] newatts = new String[atts.length][atts.length];
		Hashtable<String, Attribute> attHash = R.getAttributes();
		for (int i = 0; i < atts.length; i++)
		{
			Attribute att = attHash.get(atts[i]);
			newatts[i][0] = atts[i];
			newatts[i][1] = att.getType();
			newatts[i][2] = att.getLength();
			newatts[i][0] = att.getIsNullable();
		}
		
		IteratorInterface iterator = new TableScan(bm, R);
		
		/**
		 * use TableScan to scan tuple by tuple. Then form the values to be inserted
		 */ 
		String [][] query = new String[2][atts.length];
		// put attribute name
		for (int i = 0; i < atts.length; i++)
		{
			query[0][i] = newatts[i][0];
		}
		
		Tuple tuple = iterator.next();
		int tupleLength = Utility.getTotalLength(R.getAttribute());
		if (tuple != null)
		{
			// get the values for projected attributes and put them in query[][]
			
			Block block = tuple.getBlock();
			int offset = tuple.getOffset();
			byte [] data = block.getTupleContent(offset, tupleLength);
			String [] results = Utility.convertTupleToArray(attHash, data);
			// TODO: need to extract the projected data and insert it into query. Then call insertQuery in main
			tuple = iterator.next();
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
