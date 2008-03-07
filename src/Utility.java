import java.util.*;

/**
 * @author chrisb
 *
 */
public class Utility {

	/**
	 * Return a block ID from fileID and the block number relative to 
	 * the beginning of the file.
	 * 
	 * REMEMBER: to make it easy. I recommend you to use autoincrement 
	 * number to name the data files and index files.
	 * Then the file number can be combined with block number as a unique 
	 * identifier of a block.
	 * 
	 * @Return - blockID
	 */ 
	public static long combine(int fileID, int block_num) {
		
		return ((fileID & 0xffffffffL) << 32 | (block_num & 0xffffffffL));
		
	}
		
	/**
	 * Given a block ID, return the file ID (which is file name, i.e filename "1") 
	 * and the block number relative to the beginning of the file.
	 */ 
	public static int[] split(long blockID)
	{
		int[] larr = {(int)(blockID >> 32), (int)blockID};
		return larr; 
		
	}
	
	/**
	 * found at
	 * http://forum.java.sun.com/thread.jspa?threadID=628082&messageID=3599007
	 * @param b byte array of size 4
	 * @return the int
	 */
	public static final int makeIntFromByte4(byte[] b) {
		
		return b[0]<<24 | (b[1]&0xff)<<16 | (b[2]&0xff)<<8 | (b[3]&0xff);
		
	}
	
	/**
	 * found at
	 * http://forum.java.sun.com/thread.jspa?threadID=628082&messageID=3599007
	 * @param i int to split
	 * @return the byte array of size 4
	 */
	public static final byte[] makeByte4FromInt(int i) {
		
		return new byte[] { (byte)(i>>24), (byte)(i>>16), (byte)(i>>8), (byte)i };
		
	}
	
	/**
	 * Given block_number, compute the offset of the beginning of the block
	 * relative to the beginning of a file. 
	 * i.e. offset = block_num*BLOCK_SIZE
	 */
	public int offset(int block_num)
	{
		
		return 0;
		
	}
		
	/**
	 * Analyze the blockID and use the system catalog to obtain the relation
	 * info for the given blockID
	 */ 
	public RelationInfo getRelationInfo(int blockID)
	{
		return new RelationInfo();
		
	}

	/**
	 * Analyze the blockID and use the system catalog to obtain the index
	 * info for the given blockID
	 */ 
	public IndexInfo getIndexInfo(int blockID)
	{
		return new IndexInfo();
	}
	
	/**
	 * get length of the tuple given the Hashtable of attributes
	 * @param atts
	 * @return
	 */
	public static int getTotalLength(Hashtable<String, Attribute> atts)
	{
		Enumeration e = atts.elements();
		int len = 0;
		while(e.hasMoreElements())
		{
			Attribute att = (Attribute)e.nextElement();
			len = len + Integer.parseInt(att.getLength());
		}
		return len + Parameters.TUPLE_HEADER_SIZE;
	}
	
	/**
	 * It will convert data from insert query into array of byte of the tuple to be written to
	 * file and block. If the field is not found in the query, it will leave blank using the
	 * length from attribute object.
	 * 
	 * First, it create blank array using total length. Then it go through each 
	 * @param fields
	 * @param data
	 * @param atts
	 * @return
	 */
	public static byte [] dataToByte(String [] fields, String [] data, Hashtable<String, Attribute> atts)
	{
		int len = Utility.getTotalLength(atts);
		byte [] dataArray = new byte[len];
		
		String [] attNames = Utility.getAttributeNames(atts);
		
		byte [] deleted = Utility.makeByte4FromInt(0);
		dataArray[0] = deleted[0];
		dataArray[1] = deleted[1];
		dataArray[2] = deleted[2];
		dataArray[3] = deleted[3];
		Attribute [] attArray = hashtableToArray(atts);
		int pos = 4;
		for (int i = 0; i < attNames.length; i++)
		{
			Attribute att = atts.get(attNames[i]);
			int ind = searchStringArray(attNames[i].trim(), fields);
			if (ind != -1) 
			{
				//System.out.println("field " + att.getName().trim() + " is found");
				if (att.getType().equals("int"))
				{
					//System.out.println("field type is int");
					int temp = Integer.parseInt(data[ind]);
					byte [] tempData = Utility.makeByte4FromInt(temp);
					dataArray[pos+0] = tempData[0];
					dataArray[pos+1] = tempData[1];
					dataArray[pos+2] = tempData[2];
					dataArray[pos+3] = tempData[3];
					pos = pos + 4;
				}
				else
				{
					//System.out.println("field type is string with length " + Integer.parseInt(att.getLength()));
					int attLen = Integer.parseInt(att.getLength());
					byte [] tempData = data[ind].getBytes();
					for (int j = 0; j < tempData.length; j++)
					{
						dataArray[pos+j] = tempData[j];
					}
					pos = pos + attLen;
				}
			}
			else
			{
				//System.out.println("field " + att.getName().trim() + " is not found");
			}
		}
	
		return dataArray;
	}
	
	/**
	 * convert Hashtable to sorted array. 
	 * @param atts Hashtable of attributes  
	 * @return
	 */
	public static Attribute [] hashtableToArray(Hashtable<String, Attribute> atts)
	{
		Attribute [] attArray = new Attribute[atts.size()];
		Enumeration e = atts.elements();
		while (e.hasMoreElements())
		{
			Attribute att = (Attribute)e.nextElement();
			int ind = Integer.parseInt(att.getId());
			attArray[ind] = att;
		}
		return attArray;
	}
	
	/**
	 * look for a string in an array
	 * @param str
	 * @param arr
	 * @return
	 */
	public static int searchStringArray(String str, String [] arr)
	{
		for(int i = 0; i < arr.length; i++)
		{
			if (arr[i].equals(str)) return i;
		}
		return -1;
	}
	
	
	/**
	 * return name of attributes give the hashtable of attribute
	 * @param atts
	 * @return
	 */
	public static String [] getAttributeNames(Hashtable<String, Attribute> atts)
	{
		int len = atts.size();
		String [] strs = new String[len];
		Enumeration e = atts.elements();
		Attribute att;
		int ind;
		while(e.hasMoreElements())
		{
			att = (Attribute)e.nextElement();
			ind = Integer.parseInt(att.getId());
			strs[ind] = att.getName();
		}
		return strs;
	}
	
	/**
	 * it convert array of byte into array of string of the tuple
	 * 1. get the array of attribute name
	 * 2. how do we map it into the order of 
	 * @param atts
	 * @param data
	 * @return
	 */
	public static String [] convertTupleToArray(Hashtable atts, byte [] data)
	{
		String [] results = new String[atts.size()];
		Attribute [] attArray = new Attribute[atts.size()];
		Enumeration e = atts.elements();
		String type;
		while (e.hasMoreElements())
		{
			Attribute att = ((Attribute)e.nextElement());
			int i = Integer.parseInt(att.getId().trim());
			attArray[i] = att;
		}
		int pos = 4;
		for (int i = 0; i < attArray.length; i++)
		{
			type = attArray[i].getType();
			// if it's integer, convert 4 bytes to integer
			if (type.equals("int"))
			{
				byte [] tempData = {data[pos+0], data[pos+1], data[pos+2], data[pos+3]};
				pos = pos + 4;
				String tempField = Integer.toString(Utility.makeIntFromByte4(tempData));
				results[i] = tempField;
			}
			else
			{
				int len = Integer.parseInt(attArray[i].getLength().trim());
				byte [] tempData = new byte[len];
				for (int j = 0; j < len; j++)
				{
					tempData[j] = data[pos+j];
				}
				pos = pos + len;
				results[i] = new String(tempData);
			}
		}
		
		return results;
 	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	    Utility u = new Utility();
	    
	    Integer i1 = new Integer(1000);
	    Integer i2 = new Integer(512);
	    Long l = new Long(u.combine(i1, i2));
        int[] temp = u.split(l);
	    System.out.println("i1: " + Integer.toBinaryString(i1));
	    System.out.println("i2: " + Integer.toBinaryString(i2));
	    System.out.println(" l: " + Long.toBinaryString(l));
	    System.out.println(temp[0] + ", " + temp[1]);

	}

}
