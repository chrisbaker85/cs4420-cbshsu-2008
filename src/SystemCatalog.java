import java.util.Hashtable;

public class SystemCatalog {

	/**
	 * @param args
	 */
	
	// private Hashtable attributeCatalog;
	private String db_name;
	private Hashtable<String, RelationInfo> relationCatalog;
	
	public SystemCatalog(String db_name)
	{
		this.db_name = db_name;
		relationCatalog = new Hashtable();
	}
	/*
	public Hashtable getAttributeCatalog()
	{
		return this.attributeCatalog;
	}
	public void addAttributeCatalog(String key, Attribute attObj)
	{
		this.attributeCatalog.put(key, attObj);
	}
	*/
	
	public Hashtable getRelationCatalog()
	{
		return this.relationCatalog;
	}
	
	public void addRelationCatalog(String key, RelationInfo relObj)
	{
		this.relationCatalog.put(key, relObj);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
