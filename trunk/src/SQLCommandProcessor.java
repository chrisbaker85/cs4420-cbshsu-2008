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
	
	String db_name = "db1";
	
	public SQLCommandProcessor() {
		
		//  For testing
		qe.createDB(db_name);
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

			//attributes[i][0] = attrs[i].substring(0, attrs[i].indexOf(" "));
			//attributes[i][1] = attrs[i].substring(attrs[i].indexOf(" ") + 1, attrs[i].length());
			
		}
		
		/*
		System.out.println("[" + table_name + "]");
		for (int i = 0; i < attributes.length; i++) {
			for (int j = 0; j < attributes[0].length; j++) {
				System.out.print(attributes[i][j] + "|");
			}
			//System.out.print("[" + attributes[i][0] + "|" + attributes[i][1] + "]");
			
		}
		*/
		
		
		qe.createTable(db_name, table_name, attributes);
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
		
		System.out.println("[" + index_name + "][" + table_name + "][" + field_name + "]");
		
		qe.createIndexQuery(index_name, table_name, field_name, duplicates);
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
		
		/*
		System.out.println(table_name);
		for (int i = 0; i < attributes.length; i++) {
			
			System.out.print("[" + attributes[i][0] + "|" + attributes[i][1] + "]");
			
		}
		*/
		
		qe.insertQuery(table_name, attributes);
		
		return "";
	}
	
	/**
	 * Parses a "select x from table" query
	 * @param command the string input, checked for syntax
	 * @return some output
	 */
	public String parseTableSelect(String command) {
		
		String table_name;
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
		
		//System.out.println("[fields: " + fields + "]");
		
		/*
		for (int i = 0; i < fields.length; i++) {
		
			System.out.println(fields[i]);
			
		}
		*/
		
		temp = temp.substring(temp.indexOf(" from ") + 6);
		
		// There is no where clause, so just extract the table name
		if (temp.indexOf(" ") == -1) {
			
			table_name = temp;
			where_pairs = null;
			
		} else {
		
			// Extract the table name
			table_name = temp.substring(0, temp.indexOf(" "));
			
			// Also, extract the where conditions
			temp = temp.substring(temp.indexOf(" where ") + 7);
			where = new String[command.split("=").length - 1][2];
			                     
			where_pairs = temp.split(",( )?");
		
		}
		
		
		//System.out.println("[" + table_name + "]");
		if (where_pairs != null) {
			
			for (int i = 0; i < where_pairs.length; i++) {
				
				//System.out.println("[" + where_pairs[i] + "]");
				where_temp = where_pairs[i].split("( )?=( )?");
				where[i][0] = where_temp[0];
				where[i][1] = where_temp[1];
				//System.out.println("[" + where[i][0] + "/" + where[i][1] + "]");
				
			}
		}
		
		qe.selectQuery(table_name, fields, where);
		
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
		
		qe.selectIndexQuery(table_name, index_name);
		
		return "";
	}
	
	/**
	 * Parses a "select * from catalog" query
	 * @param command checked for syntax
	 * @return
	 */
	public String parseCatalogSelect(String command) {
		
		qe.selectCatalogQuery();
		
		return "";
	}
	
	/**
	 * UNUSED
	 * @param command
	 * @return
	 */
	public String parseSelect(String command) {
		/*
		String table_name;
		String temp;
		
		temp = command.substring(7);
		
		qe.selectQuery(table_name, fields, where);
		*/
		
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
		cp.parseInsert("insert into students (name, age) values (sovandy, 24)");
		cp.parseInsert("insert into students (name, age) values (chris, 22)");
		cp.parseInsert("insert into students (name, age) values (sami, 25)");
		cp.parseInsert("insert into students (name, age) values (aaron, 1)");
		cp.parseInsert("insert into students (name, age) values (alice, 2)");
		cp.parseInsert("insert into students (name, age) values (benjamin, 3)");
		cp.parseInsert("insert into students (name, age) values (bethany, 4)");
		cp.parseInsert("insert into students (name, age) values (cory, 5)");
		cp.parseInsert("insert into students (name, age) values (callie, 6)");
		cp.parseInsert("insert into students (name, age) values (derrick, 7)");
		cp.parseInsert("insert into students (name, age) values (deborah, 8)");
		cp.parseInsert("insert into students (name, age) values (Ernest, 8)");
		cp.parseInsert("insert into students (name, age) values (Erika, 9)");
		cp.parseInsert("insert into students (name, age) values (Freddie, 10)");
		cp.parseInsert("insert into students (name, age) values (Fran, 11)");
		cp.parseInsert("insert into students (name, age) values (Geraldo, 12)");
		cp.parseInsert("insert into students (name, age) values (Ginny, 13)");
		cp.parseInsert("insert into students (name, age) values (Herbert, 14)");
		cp.parseInsert("insert into students (name, age) values (hannah, 15)");
		cp.parseInsert("insert into students (name, age) values (Ian, 16)");
		cp.parseInsert("insert into students (name, age) values (Ida, 17)");
		cp.parseInsert("insert into students (name, age) values (Jeremy, 18)");
		cp.parseInsert("insert into students (name, age) values (Jenn, 19)");
		cp.parseInsert("insert into students (name, age) values (Kyle, 20)");
		cp.parseInsert("insert into students (name, age) values (Kiera, 21)");
		cp.parseInsert("insert into students (name, age) values (Luther, 22)");
		cp.parseInsert("insert into students (name, age) values (Lauren, 23)");
		//cp.parseTableSelect("select * from students where name = chris");
		cp.parseTableSelect("select * from students");
		
		
		
		
		
		
	}

}
