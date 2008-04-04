import java.util.ArrayList;
/**
 * 
 */

/**
 * @author chrisb
 * The OpTree is an object that builds a query tree.
 * An OpTree can be optimized and executed one op at
 * a time.
 *
 */
public class OpTree {

	/**
	 * State variable holds the state of the tree
	 * 0 - incomplete and unoptimized
	 * 1 - complete and unoptimized
	 * 2 - complete and optimized
	 */
	private int state;
	
	/**
	 * The operation tree structure
	 */
	private Op tree_root;
	
	/**
	 * Holds the collection of operation object references
	 * for easy looping 
	 */
	private ArrayList<Op> opList = new ArrayList<Op>();
	
	/**
	 * The system catalog to use in verifying and optimizing
	 */
	private SystemCatalog sc = null;
	
	/**
	 * Tells whether this query is syntactically valid or not 
	 */
	private boolean valid = false;
	
	/**
	 * Default constructor sets up tree based on parsed query
	 * @param table_names - the names of the tables (FROM) (required)
	 * @param fields - the names of the fields (SELECT) (required)
	 * @param where - the comparisons (WHERE) (optional)
	 */
	OpTree(SystemCatalog sc, String[] table_names, String[] fields, String[][] where) {

		// Set the tree's state
		this.state = 0;

		// Set a reference to the system catalog to use
		this.sc = sc;
		
		// Verify that all relations used in the query are valid
		if (this.validateRelationNames(table_names)) this.valid = true;
		
		// Set the root, a project operation
		this.tree_root = this.addOp(new OpProject(fields));
		
		if (where != null && where.length > 0) {
			
			// Set the second level, a select operation
			this.tree_root.children[0] = this.addOp(new OpSelect(where));
			
			// Set the third level
			// TODO: consider one-table query
			this.tree_root.children[0].children[0] = this.addOp(new OpCrossProduct(table_names));
			
		} else {
			
			// Set the second level b/c there are no selections to make
			this.tree_root.children[0] = this.addOp(new OpCrossProduct(table_names));
			
		}
		
		
		if (this.validateAttributes(table_names)) this.valid = true;
		
		System.out.println(this.toString());
		
	}
	
	/**
	 * Validates the names of the tables
	 * @return true if valid, false if not
	 */
	private boolean validateRelationNames(String[] table_names) {
		
		// Loop through each table name given in the query.  If it does not
		// exist, return false;
		for (int i = 0; i < table_names.length; i++) {
			
			if (!this.sc.getRelationCatalog().containsKey(table_names[i])) return false;
			
		}
		
		return true;
		
	}
	
	/**
	 * Loop through every attribute specified in the query and verify
	 * that they are in the database.
	 * @return true if all attributes are valid, else false
	 */
	private boolean validateAttributes(String[] table_names) {
		
		boolean error = false;
		
		// Using the populated ArrayList to analyze each attribute
		// individually and update it if needed
		for (int i = 0; i < this.opList.size(); i++) {
			
			Op op = this.opList.get(i);
			
			if (op instanceof OpSelect) {
				
				// If the op is a select, handle the attributes for
				// each condition
					
				// contents is an 2-D array of relation names
				String[][] att = ((String[][])op.contents);
				
				// Loop through the comparisons
				for (int j = 0; j < att.length; j++) {
				
					// Call method to verify existence of the attribute
					String table_name = verifyAttribute(att[j][0], table_names);
					
					if (table_name == null) {
						error = true;
					} else {
						att[j][0] = (table_name + "." + att[j][0]);
					}
					
				}
					
			} else if (op instanceof OpProject) {
				
				// contents is a String array of attribute names
				String[] att = (String[])(op.contents);

				// contents is null if the user indicated * in the SQL query
				// need to project ALL fully qualified attributes of ALL the tables
				if (att == null) {
					
					enumerateFields(table_names, i);
					
				} else{
					
					// TODO: Handle verification here for each attribute
					for (int j = 0; j < att.length; j++) {
						
						// Call method to verify existence of the attribute
						String table_name = verifyAttribute(att[j], table_names);
						
						if (table_name == null) {
							error = true;
						} else {
							att[j] = table_name + "." + att[j]; 
						}
						
					}
					
				}

				
			} else if (op instanceof OpIndexBasedJoin || op instanceof OpJoin || op instanceof OpSortBasedJoin) {
				
				// TODO: NEED MORE HERE!!!!!
				// TODO: FINISH THE METHOD!!
				// TODO: AHHHHHHHHHHHHHHH!!!

				
			}
			
		}
		
		return !error;
		
	}
	
	/**
	 * Check to make sure the attribute specified exists. If it does, get the
	 * table name (even though it may already be fully qualified)
	 * @param attr The attribute to check
	 * @param table_names The relations to check in
	 * @return null or relation name (String)
	 */
	private String verifyAttribute(String attr, String[] table_names) {
		
		boolean error = false;
		String table_name = null;
		
		if (attr.contains(".")) {
			
			// Get the table name from the F.Q. attribute					
			table_name = getTableNameFromFQAttribute(attr);
			
			// Check to make sure the table specified contains the attribute
			if (!((RelationInfo)this.sc.getRelationCatalog().get(table_name)).hasAttribute(attr.substring(attr.indexOf("."), attr.length()))) error = true; 
			
		} else {
			
			// Attempt to resolve the relation name
			table_name = this.fullyQualifyAttributeName(attr, table_names);
			
			if (table_name == null) {
				error = true;
			} else {
				
			}
			
		}
		
		if (error) return null;
		return table_name;
		
	}
	
	public static String getTableNameFromFQAttribute(String attr) {
	
		return attr.substring(0, attr.indexOf("."));
		
	}
	
	
	/**
	 * Fully qualify the attribute given.  If there is an error (attribute DNE, or
	 * is ambiguous, return null.  If o.k., return table name
	 * @param attr the attribute we are resolving
	 * @param table_names the tables we're checking in
	 * @return null or table name (String)
	 */
	private String fullyQualifyAttributeName(String attr, String[] table_names) {
		
		boolean error = false;
		String instanceTable = null;
		int instances = 0;
		// No table name is specified for this attribute,
		// so we need to resolve it (add [relation_name.] to attribute)
		
		// Scan through each table to see if it contains this attribute
		for (int j = 0; j < table_names.length; j++) {
			
			// Check if it exists in the jth table in the system catalog
			if (((RelationInfo)this.sc.getRelationCatalog().get(table_names[j])).hasAttribute(attr)) {
				
				instances++;
				instanceTable = table_names[j];
				
			}

		}
		
		// Make sure that it exists exactly once
		if (instances == 0) {
			
			error = true;
			
			// Attribute DNE
			System.out.println("ERROR: " + attr + " is not a valid attribute");
			
		} else if (instances > 1) {
			
			error = true;
			
			// Attribute specification is ambiguous
			System.out.println("ERROR: " + attr + " is ambiguous. Table must be specified.");
			
		}
		
		if (error) return null;

		// else
		return instanceTable;
		
	}
	
	/**
	 * This function collects all the attributes from every relation for
	 * the situation where the user has specified select *
	 * @param table_names the names of the tables in the query
	 * @param opIndex the index of the operator we're working on
	 */
	private void enumerateFields(String[] table_names, int opIndex){
		
		ArrayList<String> atts = new ArrayList<String>();
		
		// Loop through all the tables and get attribute names
		for (int j = 0; j < table_names.length; j++) {
			
			Object[] ats = ((RelationInfo)this.sc.getRelationCatalog().get(table_names[j])).getAttributesAsArray();
			
			for (int k = 0; k < ats.length; k++) {
				
				atts.add(table_names[j] + "." + (String)ats[k]);
				
			}
		}
		
		// Fill in the operator contents with the attribute objects 
		this.opList.get(opIndex).contents = new String[atts.size()];
		
		for (int j = 0; j < atts.size(); j++) {
			
			((String[])this.opList.get(opIndex).contents)[j] = atts.get(j);
			
		}
		
	}
	
	/**
	 * holds the algorithm that optimizes the query tree 
	 */
	public void optimize() {
		
		// TODO: write optimization algorithm
		
	}
	
	/**
	 * Wrapper function to use when adding an Op to the tree
	 * @param op Op[eration] object
	 * @return the same op object passed in
	 */
	private Op addOp(Op op) {
		
		this.opList.add(op);
		return op;		
		
	}
	
	/**
	 * Returns the next operation in the query plan
	 * @return an Op[eration] to perform
	 */
	public Op nextOp() {
		
		// TODO: Implement nextOp
		return new OpTable("table");
		
	}
	
	public String toString() {
		
		String output = this.tree_root.toString();
		
		return output;
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// Test a simple example
		String[] table_names = {"students", "courses"};
		String[] fields = {"name, title"};
		//String[] where = null;
		String[][] where = {{"A", "=", "B"}, {"C", ">", "D"}};
		
		OpTree ot = new OpTree(null, table_names, fields, where);
		
		System.out.println(ot.toString());

	}

}