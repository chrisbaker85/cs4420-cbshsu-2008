package systemCatalog;

public class Attribute {

	private String name;
	private String type;
	private String isNullable;
	private String relationName;
	private String id;
	private String numValues;
	
	void setName(String name) {
		
		this.name = name;
		
	}
	
	String getName() {
		
		return this.name;
		
	}
	
	void setType(String type) {
		
		this.type =  type;
		
	}
	
	String getType() {
		
		return this.type;
		
	}
	
	void setIsNullable(String isNullable) {
		
		this.isNullable = isNullable;
		
	}
	
	String getIsNullable() {
		
		return this.isNullable;
		
	}
	
	void setRelationName(String relationName) {
		
		this.relationName = relationName;
		
	}
	
	String getRelationName() {
		
		return this.relationName;
		
	}
	
	void setId(String id) {
		
		this.id = id;
		
	}
	
	String getId() {
		
		return this.id;
		
	}
	
	void setNumValues(String numValues) {
		
		this.numValues = numValues;
		
	}
	
	String getNumValues() {
		
		return this.numValues;
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
