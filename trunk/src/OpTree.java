import java.util.ArrayList;
import java.util.Hashtable;
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
	 * -1 - query invalid
	 *  0 - complete and unoptimized
	 *  1 - complete and optimized
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
	protected ArrayList<Op> opList = new ArrayList<Op>();
	
	/**
	 * The system catalog to use in verifying and optimizing
	 */
	private SystemCatalog sc = null;
	
	/**
	 * Tells whether this query is syntactically valid or not 
	 */
	private boolean valid = false;
	
	/**
	 * Create an array of Op objects
	 */
	private ArrayList<Op> queryList = null;
	
	/**
	 * Incremented to point to next operation to hand over
	 */
	private int opCursor = -1;
	
	/**
	 * Keep track of the next operator UID
	 */
	private int currOpID = 0;
	
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
		
		// Create a basic tree for the query
		this.createBaseTree(table_names, fields, where);

		// Verify that all relations used in the query are valid
		if (this.validateRelationNames(table_names)) this.valid = true;
		
		// Verify that all attributes used are valid
		if (this.validateAttributes(table_names)) this.state = 0;
		
		//if (Debug.get().debug()) System.out.println(this.toString());
		
		// identify joins
		makeJoins();
		
		//if (this.state > -1  && Debug.get().debug()) System.out.println(this.toString());
		
		// Push down select operators to just above their respective tables
		pushDownSelects();

		//if (this.state > -1  && Debug.get().debug()) System.out.println(this.toString());
		
		// Give all the leaf nodes (tables) their corresponding RelationInfo
		bindRelationInfos();
		
		if (this.state > -1  && Debug.get().debug()) System.out.println(this.toString());
		
		printOperatorList();
		
		
	}
	
	private void createBaseTree(String[] table_names, String[] fields, String[][] where) {
		
		Op curr;
		
		// Set the root, a project operation
		this.tree_root = this.addOp(new OpProject(fields, null, this));
		curr = this.tree_root;
		
		if (where != null && where.length > 0) {
			
			// daisy-chain the select operators
			for (int i = 0; i < where.length; i++) {
			
				// Set the "second" level, a select operation
				curr.children[0] = this.addOp(new OpSelect(where[i], curr, this));
				curr = curr.children[0];
				
			}
			
			// Set the third level
			// Consider a query on single relation
			//System.out.println(table_names.length);
			if (table_names.length > 1) {
		
				curr.children[0] = this.addOp(new OpCrossProduct(table_names, curr, this));
				curr = curr.children[0];
				
			} else {
				
				curr.children[0] = this.addOp(new OpTable(table_names[0], curr, this));
				curr = curr.children[0];
			}
			
			
		} else {
			
			// Set the second level b/c there are no selections to make
//			this.tree_root.children[0] = this.addOp(new OpCrossProduct(table_names));
			
			if (table_names.length > 1) {
				
				this.tree_root.children[0] = this.addOp(new OpCrossProduct(table_names, this.tree_root, this));
				
			} else {
				
				this.tree_root.children[0] = this.addOp(new OpTable(table_names[0], this.tree_root, this));
			}
			
		}
		
	}
	
	/**
	 * Pushes down the select operators
	 * Assuming that all the selects are data conditions
	 * and all table conditions (for joins) are already removed
	 */
	private void pushDownSelects() {
		
		if (Debug.get().debug()) System.out.println("INFO: inside pushDownSelects");
		
		for (int i = 0; i < this.opList.size(); i++) {
			
			Op sel = this.opList.get(i);
			
			if (sel instanceof OpSelect && (true || !((String[])(sel.contents))[1].contains("."))) {
				
				if (Debug.get().debug()) System.out.println("INFO: found a select to push down");
				
				String tableName = Utility.getTable((String)(((String[])sel.contents)[0]));
				OpTable opt = this.getTable(tableName);
				
				if (Debug.get().debug()) System.out.println("INFO: found corresponding table: " + opt);
				
				// Insert the select above the table
				if (opt != null) {
					
					opt.parent.swapChildren(sel, sel.children[0]);
					System.out.println("INFO: injecting " + sel + " above " + opt);
					opt.parent.injectAboveChild(sel, opt);
					
//					Op tableParent = opt.parent;
//					sel.parent.swapChildren(sel, sel.children[0]);
//					
//					if (Debug.get().debug()) System.out.println("INFO: select removed from tree");
//					
//					sel.children[0] = opt;
//					sel.parent = tableParent;
//					
//					opt.parent = sel;
//					
//					tableParent.swapChildren(opt, sel);
					
					if (Debug.get().debug()) System.out.println("INFO: select inserted into tree");
					
				}
				
			}
			
		}
		
	}
	
	public OpTable getTable(String tableName) {
		
		for (int i = 0; i < this.opList.size(); i++) {
//			
			if (Debug.get().debug()) System.out.println("INFO: is " + tableName + " in " + this.opList.get(i) + "?"); 
//			if (Debug.get().debug()) System.out.println("INFO: found a select to push down");
//			if ((this.opList.get(i) instanceof OpTable) && ((String)(this.opList.get(i).contents)).equals(tableName)) {
//				
//				return (OpTable)(this.opList.get(i));
//				
//			}
			
			if ((this.opList.get(i) instanceof OpCrossProduct) || (this.opList.get(i) instanceof OpJoin)) {
			
				for (int j = 0; j < this.opList.get(i).children.length; j++) {
					
					if ((this.opList.get(i).children[j] instanceof OpTable)) {
						
						return (OpTable)(this.opList.get(i).children[j]);
						
					}
					
				}
				
			}
			
		}
		
		return null;
		
	}
	
	/**
	 * Convert cross products to joins if there exists an
	 * appropriate condition
	 * This function assumes that there is one cross product
	 * operator in the tree, if one exists at all.
	 */
	private void makeJoins() {
		
		// Loop through all the operator nodes
	    for (int i = 0; i < this.opList.size(); i++) {

	        Op xprod = null;
	        Op[] tables;
	        Op sel = this.opList.get(i);
	        
	        // If this node is a select operator, find any conditions
	        // that create a join and remove them, putting them in the proper join op
	        if ((sel instanceof OpSelect)) {
	        	
	        	if (Debug.get().debug()) System.out.println("INFO: found a select: " + sel);
	        	if (Debug.get().debug()) System.out.println("INFO: finding an xprod with: " + (String[])sel.contents);
	        	if (((String[])sel.contents)[1].contains(".") && (xprod = this.crossProductExistsWith((String[])sel.contents)) != null) {
	        		
		        	/**
		        	 * Making the following assumptions here:
		        	 * 1) A select operator will never be the root of the tree
		        	 *    (i.e. it will always have a non-null parent)
		        	 * 2) A s
		        	 * elect operator will never be a "sibling"
		        	 *    (i.e it will never share a parent with another operator)
		        	 * 3) A select operator will only have one child
		        	 */ 
		        	
	        		sel.parent.swapChildren(sel, sel.children[0]);
	        		if (Debug.get().debug()) System.out.println("INFO: Effectively removed sel from top of table");
	        		if (Debug.get().debug()) System.out.println("INFO: handling Join with: " + ((String[])(sel.contents))[0] + "/" + ((String[])(sel.contents))[1] + "/" + ((String[])(sel.contents))[2]);
		        	((OpCrossProduct)xprod).addJoin((String[])sel.contents);
		        	
		        	if (Debug.get().debug()) System.out.println("INFO: Removing " + sel);
//		        	this.opList.remove(sel);
		        	
	        	}

	        }
	        
	    }
		
		
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
			
			// Only need to check for select and project operators
			if (op instanceof OpSelect) {
				
				// contents is an 2-D array of relation names
				String[] att = ((String[])op.contents);
				
				// Call method to verify existence of the attribute
				String table_name = verifyAttribute(att[0], table_names);
				
				if (table_name == null) {
					error = true;
				} else {
					att[0] = (table_name + "." + att[0]);
				}
				
				if (!att[1].startsWith("'")) {
					
					// Call method to verify existence of the attribute
					table_name = verifyAttribute(att[1], table_names);
					
					if (table_name == null) {
						error = true;
					} else {
						att[1] = (table_name + "." + att[1]);
					}
					
				} else {
					
					att[1] = att[1].substring(1, att[1].length() - 1);
					
				}
					
			} else if (op instanceof OpProject) {
				
				// contents is a String array of attribute names
				String[] att = (String[])(op.contents);

				// contents is null if the user indicated * in the SQL query
				// need to project ALL fully qualified attributes of ALL the tables
				if (att == null) {
					
					enumerateFields(table_names, i);
					
				} else{
					
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
			table_name = Utility.getTable(attr);
			
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
			
			Hashtable ht = this.sc.getRelationCatalog();
			RelationInfo ri = (RelationInfo)ht.get(table_names[j]);
			boolean hasa = ri.hasAttribute(attr);
			
			if (hasa) {
				
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
	
	private Op crossProductExistsWith(String[] x) {
	    
		Op op;
		
	    for (int i = 0; i < this.opList.size(); i++) {
	        
	    	op = this.opList.get(i);
	    	if (Debug.get().debug()) System.out.println("INFO: looking for " + Utility.getTable(x[0]) + " and " + Utility.getTable(x[1]) + " in " + op);
	        if ((op instanceof OpCrossProduct) && (op.containsTable(Utility.getTable(x[0])) > 0) && (op.containsTable(Utility.getTable(x[1])) > 0)) {
	            
	            return op;
	            
	        }
	        
	    }
	    
	    return null;
	}
	
	/**
	 * Wrapper function to use when adding an Op to the tree
	 * @param op Op[eration] object
	 * @return the same op object passed in
	 */
	protected Op addOp(Op op) {
		
		op.setID(this.currOpID);
		this.currOpID++;
		this.opList.add(op);
		return op;		
		
	}
	
	/**
	 * Returns the next operation in the query plan
	 * @return an Op[eration] to perform
	 */
	public Op nextOp() {
		
		if (this.queryList == null) this.queryList = this.generateQueryPlan(this.tree_root);

		// Increment the cursor
		opCursor++;
		
		if (this.opCursor < this.queryList.size()) {
			
		    if (Debug.get().debug()) System.out.println("INFO: LIST SIZE: " + this.opList.size() + "/CURSOR: " + this.opCursor + " is a: " + this.queryList.get(this.opCursor).getType());
			return this.queryList.get(this.opCursor);
			
		}
		
		return null;
		
	}
	
	/**
	 * Resets the op cursor so that the query plan can be accessed again
	 */
	public void resetOpList() {
		
		this.opCursor = -1;
		
	}
	
	/**
	 * Generates a query plan (order of operations)
	 * 0) Start at the tree-root
	 * 1) Climb down the tree until an OpTree is reached
	 * 2) Move up until an Op is hit that has another child
	 * 3) Climb down that subtree until an OpTree is reached
	 * 4) (keep going...)
	 * 
	 * biased to the left
	 */
	private ArrayList<Op> generateQueryPlan(Op op) {
		
		Op temp = op;
		ArrayList<Op> list = new ArrayList<Op>();
		int counter = 0;
		
		System.out.println("INFO: opList size: " + this.opList.size());
		
		while (counter < this.opList.size()) {

			if (temp == null) return list;
			
			// Base cases
			if (temp instanceof OpTable || !temp.hasUnvisitedChildren()) {
				
				System.out.println("INFO: using " + temp);
				temp.use();
				list.add(temp);
				
				counter++;
				temp = temp.parent;
			}
			
			// At the end, the root's parent is null, so check for that
			if (temp != null) {
				
				for (int i = 0; i < temp.children.length; i++) {
					
					
					if (temp.children[i] != null && !temp.children[i].isUsed()) {
						
						temp = temp.children[i];
						break;
						
					}
					
				}
				
			}
			
			
		}
		
		return list;
		
	}
	
	public String toString() {
		
		String output = this.tree_root.getString();
		
		return output;
		
	}
	
	public void printOperatorList() {
		
		Op op;
		
		while((op = this.nextOp()) != null) {
			
			System.out.println("INFO OPLIST " + op.getType());
			
		}
		
	}
	
	/**
	 * Get the number of operators in this tree
	 * @return number of operators
	 */
	public int getNumOps () {
		
		return this.opList.size();
		
	}
	
	/**
	 * Set the RelationInfo property for every "leaf" OpTable node
	 */
	private void bindRelationInfos() {
		
		// Loop through all the operators and find the OpTable types
		for (int i = 0; i < this.opList.size(); i++) {
			
			//System.out.println("INFO: type:" + this.opList.get(i).getType());
			
			Op op = this.opList.get(i);
			
			if (op.children != null) {
				
				for (int j = 0; j < op.children.length; j++) {

					if (op.children[j] != null && (op.children[j] instanceof OpTable)) {
						
						op.children[j].info = (RelationInfo)this.sc.getRelationCatalog().get((String)op.children[j].contents);
						if (Debug.get().debug()) System.out.println("INFO: RelationInfo set for " + op.children[j]);
						
					}
					
					
				}
				
			}

				
				
//			}
			
		}
		
		
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
