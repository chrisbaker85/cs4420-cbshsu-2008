import java.text.SimpleDateFormat;
import java.util.*;

public class RelationInfo {

	public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";

	private String name;
	private String dateCreated;
	private String dateModified;
	private String numTuples;
	private int id;
	private String colsIndexed;
	private String indexFilename;
	private String numDataBlocks;
	private Hashtable<String, Attribute> attributes = new Hashtable<String, Attribute>();
	
	/**
	 * This is the constructor
	 * @param name
	 * @param dateCreated
	 * @param dateModified
	 * @param numTuple
	 * @param id
	 * @param colsIndexed
	 * @param filename
	 * @param numDataBlocks
	 */
	public RelationInfo(String name, String dateCreated, String dateModified, String numTuple, int id, String colsIndexed, String filename, String numDataBlocks, Hashtable attributes)
	{
		this.name = name;
		this.dateCreated = dateCreated;
		this.dateModified = dateModified;
		this.numTuples = numTuples;
		this.id = id;
		this.colsIndexed = colsIndexed;
		this.indexFilename = filename;
		this.numDataBlocks = numDataBlocks;
		this.attributes = attributes;
	}
	
	public RelationInfo()
	{
		// do nothing, just test constructor
	}
	void setName(String name) {
		
		this.name = name; 
		
	}
	
	String getName() {
		
		return this.name;
		
	}
	
	void setDateCreated(String dateCreated) {
		
		this.dateCreated = dateCreated;
		
	}
	
	String getDateCreated() {
		
		return this.dateCreated;
		
	}
	
	void setDateModified(String dateModified) {
		
		this.dateModified = dateModified;
		
	}
	
	String getDateModified() {
		
		return this.dateModified;
		
	}
	
	void setNumTuples(String numTuples) {
		
		this.numTuples = numTuples;
		
	}
	
	String getNumTuples() {
		
		return this.numTuples;
		
	}
	
	void setId(int id) {
		
		this.id = id;
		
	}
	
	int getId() {
		
		return this.id;
		
	}
	
	void setColsIndexed(String colsIndexed) {
		
		this.colsIndexed = colsIndexed;
		
	}
	
	String getColsIndexed() {
		
		return this.colsIndexed;
		
	}
	
	void setFilenames(String iFilename) {
		this.indexFilename = iFilename;
		
	}

	String getFilenames() {
		
		return this.indexFilename;	

	}
	
	void setNumDataBlocks(String numDataBlocks) {
		
		this.numDataBlocks = numDataBlocks;
		
	}
	
	String getNumDataBlocks() {
		
		return this.numDataBlocks;
		
	}
	
	/**
	 * Adds an attribute to this relation
	 * @param name The Attribute name
	 * @param type The Attribute type (int/string)
	 * @param length The Attribute length)
	 * @param isN Nullable
	 */
	public void addAttribute(String name, String type, String length, boolean isN) 
	{
		String nullable = new String();
		if (isN) nullable = "yes";
		else nullable = "no";
		Attribute att = new Attribute(name, type, length, nullable, this.getName());
		this.attributes.put(name, att);
	}
	
	public Hashtable getAttribute()
	{
		return this.attributes;
	}
	
	/**
	 * Should return the byte offset for this field
	 * relative to the tuple's first byte.
	 * @param i the ith column
	 * @return the byte offset
	 */
	public int getFieldOffset(int i) {
		int j = 0;
		int offset = 0;
		
		// Iterate through the attributes until i
		for (j = 0; j < i; j++ ) {
			
			Attribute att = this.attributes.get(j);
			String type = att.getType();

			if (type.equals("str")) {
				
				// If the attribute is a string, add 4 * length
				offset += 4 * Integer.parseInt(att.getLength().trim());
				
			} else if (type.equals("int")) {

				// If the attribute is an int, add 4
				offset += 4;
				
			} else {
				
				System.out.println("invalid data type");
				
			}
	
		}
		
		return offset;
	}
	
	public void updateBlockNumber(int num)
	{
		int curNum = Integer.parseInt(numDataBlocks);
		numDataBlocks = Integer.toString(curNum + num);
	}
	
	public void updateTupleNumber(int num)
	{
		int curNum = Integer.parseInt(numTuples);
		numTuples = Integer.toString(curNum + num);
	}
	
	public void updateDateModified()
	{
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		String cur_date = (String)sdf.format(cal.getTime());
		this.dateModified = cur_date;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		RelationInfo ri = new RelationInfo();
		ri.addAttribute("first_name", "str", "20", false);
		ri.addAttribute("last_name", "str", "20", false);
		ri.addAttribute("age", "int", "0", false);
		
		System.out.println(ri.getFieldOffset(2));

	}

}
