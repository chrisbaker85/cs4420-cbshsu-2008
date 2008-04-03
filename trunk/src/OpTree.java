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
	OpTree(SystemCatalog sc, String[] table_names, String[] fields, String[] where) {

		// Set the tree's state
		this.state = 0;

		// Set a reference to the system catalog to use
		this.sc = sc;
		
		// Verify that all relations used in the query are valid
		if (this.validateRelationNames(table_names)) this.valid = true;
		
		// Set the root, a project op
		this.tree_root = this.addOp(new OpProject(fields));
		
		if (where != null && where.length > 0) {
			
			// Set the second level, a select op
			this.tree_root.children[0] = this.addOp(new OpSelect(where));
			
			// Set the third level
			// TODO: consider one-table query
			this.tree_root.children[0].children[0] = this.addOp(new OpCrossProduct(table_names));
			
		} else {
			
			// Set the second level b/c there are no selections to make
			this.tree_root.children[0] = this.addOp(new OpCrossProduct(table_names));
			
		}
		
		if (this.validateAttributes(table_names)) this.valid = true;
		
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
			
			if (/*this.opList.get(i) instanceof OpSelect ||*/ this.opList.get(i) instanceof OpProject) {
				
				String att = (String)(this.opList.get(i).contents);
				
				if (att.contains(".")) {
					
					// Check to make sure the table specified contains the attribute					
					String table_name = att.substring(0, att.indexOf("."));
					
					if (!((RelationInfo)this.sc.getRelationCatalog().get(table_name)).hasAttribute(att.substring(att.indexOf("."), att.length()))) error = true; 
					
				} else {
					
					// No table name is specified for this attribute,
					// so we need to resolve it (add [relation_name.] to attribute)
					
					// Scan through each table to see if it contains this attribute
					for (int j = 0; j < table_names.length; j++) {
						
						int instances = 0;
						String instanceTable = null;
						
						// Check if it exists in the jth table in the syscat
						if (((RelationInfo)this.sc.getRelationCatalog().get(table_names[j])).hasAttribute(att)) {
							
							instances++;
							instanceTable = table_names[j];
							
						}
						
						// Make sure that it exists exactly once
						if (instances == 0) {
							
							error = true;
							
							// Attribute DNE
							System.out.println("ERROR: " + att + " is not a valid attribute");
							continue;
							
						} else if (instances > 1) {
							
							error = true;
							
							// Attribute specification is ambiguous
							System.out.println("ERROR: " + att + " is ambiguous. Table must be specified.");
							continue;
							
						}
						
						this.opList.get(i).contents = instanceTable + "." + att;
						
					}
					
				}
				
				
			} else if (this.opList.get(i) instanceof OpIndexBasedJoin || this.opList.get(i) instanceof OpJoin || this.opList.get(i) instanceof OpSortBasedJoin) {
				
				// TODO: NEED MORE HERE!!!!! 

				
			}
			
		}
		
		return !error;
		
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
		String[] where = {"A=B", "C=D"};
		
		OpTree ot = new OpTree(null, table_names, fields, where);
		
		System.out.println(ot.toString());

	}

}
