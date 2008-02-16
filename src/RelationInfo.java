
public class RelationInfo {

	private String name;
	private String dateCreated;
	private String dateModified;
	private String numTuples;
	private String id;
	private String colsIndexed;
	// private String filename;
	private String numDataBlocks;
	
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
	public RelationInfo(String name, String dateCreated, String dateModified, String numTuple, String id, String colsIndexed, String numDataBlocks)
	{
		this.name = name;
		this.dateCreated = dateCreated;
		this.dateModified = dateModified;
		this.numTuples = numTuples;
		this.id = id;
		this.colsIndexed = colsIndexed;
		this.numDataBlocks = numDataBlocks;
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
	
	void setId(String id) {
		
		this.id = id;
		
	}
	
	String getId() {
		
		return this.id;
		
	}
	
	void setColsIndexed(String colsIndexed) {
		
		this.colsIndexed = colsIndexed;
		
	}
	
	String getColsIndexed() {
		
		return this.colsIndexed;
		
	}
	
	/*
	void setFilenames(String filename) {
		this.filename = filename;
	}
	String getFilenames() {
		return this.filename;	
	}
	*/
	
	void setNumDataBlocks(String numDataBlocks) {
		
		this.numDataBlocks = numDataBlocks;
		
	}
	
	String getNumDataBlocks() {
		
		return this.numDataBlocks;
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
