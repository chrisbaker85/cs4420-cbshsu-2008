/**
 * 
 */

/**
 * @author chrisb
 * This class parses SQL commands.  It assumes that basic syntax
 * has been verified 
 */
public class SQLCommandProcessor {
	
	QueryEngine qe = new Main();

	public boolean parseCommand(String command) {
		
		if (command.toLowerCase().startsWith("select")) {
			
			parseSelect(command);
			
		} else if (command.toLowerCase().startsWith("insert")) {
			
			parseInsert(command);
			
		} else if (command.toLowerCase().startsWith("create")) {
			
			parseCreate(command);
			
		} else {
			
			return false;
			
		}
		
		return true;

	}
	
	public String parseCreate(String command) {
		
		return "";
		
	}
	
	/**
	 * Parses a "create" query and 
	 * @param command
	 * @return
	 */
	public String parseCreateTable(String command) {
		
		String temp = command;
		String table_name;
		String[][] attributes;
		
		temp = command.substring(13);
		table_name = temp.substring(0, temp.indexOf(" "));
		
		//System.out.println(table_name);
		
		//qe.createTable("", table_name, attributes);
		return "";
		
	}
	
	public String parseCreateIndex(String command) {
		
		String temp = command;
		String index_name;
		String table_name;
		String field_name;
		
		temp = command.substring(13);
		index_name = temp.substring(0, temp.indexOf(" "));
		
		temp = temp.substring(index_name.length() + 4);
		table_name = temp.substring(0, temp.indexOf(" "));
		
		field_name = temp.substring(temp.indexOf("(") + 1, temp.indexOf(")"));
		
		System.out.println("[" + index_name + "][" + table_name + "][" + field_name + "]");
		
		//qe.createIndex("", table_name, attributes);
		return "";
		
	}
	
	public String parseInsert(String command) {
		
		
		return "";
	}
	
	public String parseSelect(String command) {
		
		
		return "";
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
