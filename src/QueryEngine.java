/**
 * 
 */

/**
 * @author chrisb
 * Abstract Class that defines the primary functionality of
 * a DBMS.
 *
 */
public interface QueryEngine {
	
	public boolean insertQuery(String table_name, String [][] query);
	public boolean createTable(String db_name, String table_name, String [][] attributes);
	public boolean createIndexQuery(String index_name, String table_name, String field_name, boolean duplicates);
	public boolean selectQuery(String table_name, String [] fields, String[][] where);
	public boolean selectIndexQuery(String table_name, String index_name);
	public boolean selectCatalogQuery();
	
}
