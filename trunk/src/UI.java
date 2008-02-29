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
			  
			  input = input.toLowerCase();
			  
			  if (input.equals("exit")) {
				  
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
	/**
	 * @param input
	 * @return
	 */
	private boolean checkSyntax(String input) {
		
		boolean b = false;
		String result;
		
		b = Pattern.matches("select ((\\*)|(([a-z]+)(,( )?[a-z]+)*)) from [a-zA-Z0-9_-]+( where ([a-zA-Z0-9_-]+( )?=( )?[a-zA-Z0-9_-]+)((,( )?[a-zA-Z0-9_-]+( )?=( )?[a-zA-Z0-9_-]+)*)?)?", input);
		if (b) {
			result = proc.parseTableSelect(input);
			return true;
		}
		
		b = Pattern.matches("select \\* from index [a-zA-Z0-9_-]+ [a-zA-Z0-9_-]+", input);
		if (b) {
			result = proc.parseIndexSelect(input);
			return true;
		}
		
		b = Pattern.matches("select \\* from catalog", input);
		if (b) {
			result = proc.parseCatalogSelect(input);
			return true;
		}
		
		b = Pattern.matches("insert into [a-zA-Z0-9_-]+ (\\(([a-zA-Z0-9_-]+(, [a-zA-Z0-9_-]+)*)\\)) values \\(([a-zA-Z0-9_-])+(, [a-zA-Z0-9_-]+)*\\)", input);
		if (b) {
			result = proc.parseInsert(input);
			return true;
		}
		
		b = Pattern.matches("create index [a-zA-Z0-9_-]+ on [a-zA-Z0-9_-]+ \\([a-zA-Z0-9_-]+\\)(( )?\\[no duplicates\\])?", input);
		if (b) {
			result = proc.parseCreateIndex(input);
			return true;
		}
		
		b = Pattern.matches("create table ([a-zA-Z0-9_-]+)( )?\\(([a-zA-Z0-9_-]+) (string|int)(((, [a-zA-Z0-9_-]+) (string|int))*)\\)", input);
		if (b) {
			result = proc.parseCreateTable(input);
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
