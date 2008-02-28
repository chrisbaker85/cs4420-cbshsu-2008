/**
 * 
 */

import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.regex.Pattern;

/**
 * @author chrisb
 * This class interacts with an end user via
 * console-based input/output
 */
public class UI {

	InputStreamReader isr = new InputStreamReader( System.in );
	BufferedReader stdin = new BufferedReader( isr );
	boolean running = true;
	SQLCommandProcessor proc = new SQLCommandProcessor();
	
	public UI() {
		
		System.out.println("/////////////////////////////////////////////\n          WELCOME TO OUR DATABASE");
		System.out.println("      a project for Databases 4420 by");
		System.out.println("  Chris Baker, Sovandy Hang and Sami Ubaissi\n/////////////////////////////////////////////");
		System.out.println("\nStart with SELECT * FROM CATALOG\ntype \"exit\" to exit.");
		
		this.run();
	}
	
	/**
	 * Contains the prompt/process loop
	 */
	private void run() {
		
		while (running) {
			
			showPrompt();
			
			try {
				
			  String input = stdin.readLine();
			  
			  if (input.equals("EXIT") || input.equals("exit")) {
				  
				  running = false;
				  break;
			  }
			  
			  process(input);
			  
			} catch (Exception e) {
				
				System.out.println(e.getMessage());
				
			}
			
		}
		
		finish();
		
	}
	
	/**
	 * Displays the user prompt
	 */
	private void showPrompt() {
		
		System.out.print("\n>>");
		
	}
	
	/**
	 * Processes any given input string, e.g. SQL command 
	 * @param input the input from the user
	 */
	private void process(String input) {
		
		if (!checkSyntax(input)) {

			System.out.println("What you entered was not a valid command.\nPlease try again or type \"exit\" to quit.");
			
		}
		
	}
	
	/**
	 * Check to make sure that the syntax is correct
	 * @param input the user's input
	 * @return boolean - syntax is valid or not
	 */
	private boolean checkSyntax(String input) {
		
		boolean b = false;
		String result;
		
		b = Pattern.matches("((SELECT)|(select)) ((\\*)|(([a-z]+)(,( )?[a-z]+)*)) ((FROM)|(from)) [a-zA-Z]+( ((WHERE)|(where)) [a-zA-Z]+( )?=( )?[a-zA-Z]+)?", input);
		if (b) {
			result = proc.parseSelect(input);
			return true;
		}
		
		b = Pattern.matches("((SELECT)|(select)) \\* ((FROM)|(from)) ((INDEX)|(index)) [a-zA-Z]+ [a-zA-Z]+", input);
		if (b) {
			result = proc.parseSelect(input);
			return true;
		}
		
		b = Pattern.matches("((SELECT)|(select)) \\* ((FROM)|(from)) ((CATALOG)|(catalog)) [a-zA-Z]+", input);
		if (b) {
			result = proc.parseSelect(input);
			return true;
		}
		
		//TODO: FIX ME
		b = Pattern.matches("((INSERT)|(insert)) ((INTO)|(into)) [a-zA-Z]+(\\(([a-zA-Z]|([a-zA-Z], ([a-zA-Z], )* ([a-zA-Z])))*\\))? ((VALUES)|(values)) \\(([a-zA-Z])+\\)", input);
		if (b) {
			result = proc.parseInsert(input);
			return true;
		}
		
		// TODO: FIX ME
		b = Pattern.matches("CREATE INDEX index_name ON table_name (attr)", input);
		if (b) {
			result = proc.parseCreate(input);
			return true;
		}
		
		// 
		b = Pattern.matches("((CREATE)|(create)) ((TABLE)|(table)) ([a-zA-Z_-]+) (([a-zA-Z_-]+) (string|int)( ,( )?[a-zA-Z_-]+)* (string|int))", input);
		if (b) {
			result = proc.parseCreate(input);
			return true;
		}
		
		return false;
		
	}
	
	/**
	 * Prints out a message telling the user that they
	 * have successfully exited the database.
	 */
	private void finish() {
		
		System.out.println("/////////////////////////////////////////////\nThe database has been closed.\n/////////////////////////////////////////////\n");
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		UI ui = new UI();
		
		
	}

}
