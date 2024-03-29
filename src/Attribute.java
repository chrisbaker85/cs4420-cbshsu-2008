import java.util.Hashtable;

public class Attribute {

	private String name;
	private String type;
	private String length;
	private String isNullable;
	private String relationName;
	private String id;
	private int numValues;
	private Hashtable<String, Integer> distinctValues = new Hashtable<String, Integer>();;
	
	public Attribute(String name, String type, String length, String nullable, String relation, String id, int numValues)
	{
		this.name = name;
		this.type = type;
		this.length = length;
		this.isNullable = nullable;
		this.relationName = relation;
		this.id = id;
		this.numValues = numValues;
	}
	
	public Attribute(String name, String type, String length, String nullable, String relation)
	{
		this.name = name;
		this.type = type;
		this.length = length;
		this.isNullable = nullable;
		this.relationName = relation;
	}
	
	void setName(String name) {
		
		this.name = name;
		
	}
	
	String getName() {
		
		return this.name;
		
	}
	
	void setType(String type) {
		
		this.type =  type;
		
	}
	
	void setLength(String num)
	{
		this.length = num;
	}
	
	public String getLength()
	{
		return this.length;
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
	
	void setNumValues(int numValues) {
		
		this.numValues = numValues;
		
	}
	
	int getNumValues() {
		
		return this.numValues;
		
	}
	
	int getDistinctValues()
	{
		return this.numValues;
	}
	
	int getDuplicateNumber(String key)
	{
		return (Integer)distinctValues.get(key);
	}
	
	void updateDistinctValues(String key)
	{
		if (distinctValues.containsKey(key))
		{
			int num = (Integer)distinctValues.get(key);
			num++;
			distinctValues.put(key, num);
		}
		else
		{
			distinctValues.put(key, 1);
			this.numValues++;
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
