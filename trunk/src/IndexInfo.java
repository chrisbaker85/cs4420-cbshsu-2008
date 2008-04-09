import java.util.*;

/**
 * @author Sovandy
 *
 */

public class IndexInfo 
{
	private String indexName;
	private String attName;
	private String tableName;
	private TreeMap index;
	
	public IndexInfo(String indexName, String attName, String tableName)
	{
		this.indexName = indexName;
		this.attName = tableName;
		this.attName = attName;
		index = new TreeMap();
	}
	
	public String getIdexName()
	{
		return this.indexName;
	}
	
	public void setIndexName(String indexName)
	{
		this.indexName = indexName;
	}
	
	public String getTableName()
	{
		return this.tableName;
	}
	
	public void setTableName(String tableName)
	{
		this.tableName = tableName;
	}
	
	public String getAttName()
	{
		return this.attName;
	}
	
	public void setAttName(String attName)
	{
		this.attName = attName;
	}
	
	public TreeMap getIndex()
	{
		return this.index;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
