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
	
	public void selectQuery(String table_name, String [] fields, String from, String where);
	public void insertQuery(String table_name, String [][] query);
	public void createTable(String db_name, String table_name, String [][] attributes);
	public void createIndexQuery(String index_name, String table_name, String field_name);
}