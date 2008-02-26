/**
 * 
 */

/**
 * @author chrisb
 * This class parses SQL commands.  It assumes that basic syntax
 * has been verified 
 */
public class SQLCommandProcessor {

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
