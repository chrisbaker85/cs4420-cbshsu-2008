/**
 * 
 */
import java.io.File;
/**
 * @author chrisb
 * This class parses SQL commands.  It assumes that basic syntax
 * has been verified 
 */
public class SQLCommandProcessor {
	
	QueryEngine qe = new Main();
	
	String db_name = "db1";
	
	public SQLCommandProcessor() {
		
		// If a database with default name exists on file
		// use it, else create it
		if (!(new File(db_name + "_relations.xml")).exists()) {
			qe.createDB(db_name);
		}
		qe.useDatabase(db_name);
		
	}

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
	 * Parses a "create table" query
	 * @param command the String query, syntax-checked
	 * @return some output
	 */
	public String parseCreateTable(String command) {
		
		String temp = command;
		String table_name;
		String[][] attributes = new String[command.split(",").length][2];
		String[] attrs;
		String[] meta;
		
		// 1) Extract the table's name that we're creating
		temp = command.substring(13);
		table_name = temp.substring(0, temp.indexOf(" "));

		// 2) Extract the attribute/type pairs
		temp = temp.substring(temp.indexOf("(") + 1, temp.indexOf(")"));
		attrs = temp.split(",( )?");
		for (int i = 0; i < attrs.length; i++) {
			
			meta = attrs[i].split(" ");
			attributes[i] = meta;
		}
		
		if (qe.createTable(db_name, table_name, attributes, false) == false) System.out.println("ERROR: create table failed");
		return "";
		
	}
	
	/**
	 * Parses a "create index" query
	 * @param command the actual query as a String
	 * @return some output
	 */
	public String parseCreateIndex(String command) {
		
		String temp = command;
		String index_name;
		String table_name;
		String field_name;
		boolean duplicates;
		
		duplicates = temp.endsWith(" [no duplicates]");
		temp = command.substring(13);
		index_name = temp.substring(0, temp.indexOf(" "));
		
		temp = temp.substring(index_name.length() + 4);
		table_name = temp.substring(0, temp.indexOf(" "));
		
		field_name = temp.substring(temp.indexOf("(") + 1, temp.indexOf(")"));
		
		//System.out.println("[" + index_name + "][" + table_name + "][" + field_name + "]");
		
		if (qe.createIndexQuery(index_name, table_name, field_name, duplicates) == false) System.out.println("ERROR: create index failed");
		return "";
		
	}
	
	/**
	 * Parses an insert query
	 * @param command The raw query, already checked for syntax
	 * @return
	 */
	public String parseInsert(String command) {
		
		String temp, temp_attr;
		String table_name;
		String[][] attributes = new String[2][(command.split(",").length + 1) / 2];
		String[] attrs;
		String[] vals;
		
		// 1) Extract table name
		temp = command.substring(12);
		table_name = temp.substring(0, temp.indexOf(" "));
		
		// 2) Extract attributes and types
		temp_attr = temp.substring(temp.indexOf("(") + 1, temp.indexOf(")"));
		attrs = temp_attr.split(", ");
		temp = temp.substring(temp.indexOf(")"));
		temp = temp.substring(temp.indexOf("(") + 1);
		temp = temp.substring(0, temp.indexOf(")"));
		
		vals = temp.split(", ");
		
		for (int i = 0; i < attrs.length; i++) {
			
			attributes[0][i] = attrs[i];
			attributes[1][i] = vals[i];
			
		}		
		if (qe.insertQuery(table_name, attributes) == false) System.out.println("ERROR: insert failed");
		return "";
	}
	
	/**
	 * Parses a "select x from table" query
	 * @param command the string input, checked for syntax
	 * @return some output
	 */
	public String parseTableSelect(String command) {
		
		String[] table_names;
		String[] fields;
		String temp;
		String[][] where = null;
		String[] where_pairs;
		String[] where_temp;
		
		temp = command.substring(7);
		fields = temp.substring(0, temp.indexOf(" from ")).split(",( )?");
		
		// If the user wants to select *, send in fields variable
		// that is null
		if (fields.length == 1 && fields[0].equals("*")) {
			
			fields = null;
			
		}
		
		temp = temp.substring(temp.indexOf(" from ") + 6);
		
		// There is no where clause, so just extract the table name
		if (!temp.contains("where")) {
			
			//table_names = new String[1];
			table_names = temp.split(", |,");
			
//			for (int i = 0; i < table_names.length; i++) {
//				
//				System.out.println("/" + table_names[i] + "/");
//				
//			}
			
			where_pairs = null;
			
		} else {
		
			// Extract the table name
			table_names = temp.substring(0, temp.indexOf(" where ")).split(",( )?");
			
			// Also, extract the where conditions
			temp = temp.substring(temp.indexOf(" where ") + 7);
			where = new String[command.split("=|<|>").length - 1][3];
			                     
			where_pairs = temp.split(",( )?");
		
		}
		
		
		//System.out.println("[" + table_name + "]");
		
		/**
		 * parses the comparisons in WHERE clause
		 * 
		 * name = joe 
		 * 
		 */
		if (where_pairs != null) {
			
			for (int i = 0; i < where_pairs.length; i++) {
				
				//System.out.println("[" + where_pairs[i] + "]");
				
				
				String compOp = "";
				
				if (where_pairs[i].contains("=")) {
					compOp = "=";
				} else if (where_pairs[i].contains(">")) {
					compOp = ">";
				} else {
					compOp = "<";
				}
				
				where_temp = where_pairs[i].split("( )?(=|>|<)( )?");
				where[i][0] = where_temp[0];
				where[i][1] = where_temp[1];
				where[i][2] = compOp;
				//System.out.println("[" + where[i][0] + "/" + where[i][1] + "/" + where[i][2] + "]");
				
			}
		}
		
		//System.out.println(table_names + "][" + fields + "][" + where);
		
//		OpTree ot = new OpTree(null, table_names, fields, where);
		
		if(qe.selectQuery(table_names, fields, where) == false) System.out.println("ERROR: select failed");
		
		return "";
	}
	
	/**
	 * Parses a "select x from index" query
	 * @param command input query, checked for syntax
	 * @return some output
	 */
	public String parseIndexSelect(String command) {
		
		String temp = command;
		String table_name;
		String index_name;
		
		temp = command.substring(20);
		table_name = temp.substring(0, temp.indexOf(" "));
		index_name = temp.substring(temp.indexOf(" ") + 1, temp.length());
		//System.out.println(table_name + "/" + index_name);
		
		if (qe.selectIndexQuery(table_name, index_name) == false) System.out.println("ERROR: index select failed");
		
		return "";
	}
	
	/**
	 * Parses a "select * from catalog" query
	 * @param command checked for syntax
	 * @return
	 */
	public String parseCatalogSelect(String command) {
		
		if (qe.selectCatalogQuery() == false) System.out.println("ERROR: select catalog failed");
		
		return "";
	}
	
	/**
	 * UNUSED
	 * @param command
	 * @return
	 */
	public String parseSelect(String command) {
		return "";
	}
	
	public void exit() {
		
		this.qe.exit();
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		SQLCommandProcessor cp = new SQLCommandProcessor();
		cp.parseCreateTable("create table students (name string 10 no, age int 4 no)");
		cp.parseCreateTable("create table students (id int 4 no, name string 15 no, department string 3 yes)");
		cp.parseInsert("insert into students (name, age) values (sovandy, 24)");
		cp.parseInsert("insert into students (id, name, department) values (1, sovandy, d1 )");
		cp.parseInsert("insert into students (id, name, department) values (2, chris, d1)");
		cp.parseInsert("insert into students (id, name, department) values (3, sami, d2)");
		cp.parseInsert("insert into students (id, name, department) values (4, aaron, d2)");
		//cp.parseTableSelect("select * from students where name = chris");
		cp.parseTableSelect("select * from students");
		
	}

}
