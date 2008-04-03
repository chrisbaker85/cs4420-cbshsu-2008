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
	 * Default constructor sets up tree based on parsed query
	 * @param table_names - the names of the tables (FROM) (required)
	 * @param fields - the names of the fields (SELECT) (required)
	 * @param where - the comparisons (WHERE) (optional)
	 */
	OpTree(String[] table_names, String[] fields, String[] where) {

		// Set the tree's state
		this.state = 0;
		
		// Set the root, a project op
		this.tree_root = this.addOp(new OpProject(fields));
		
		if (where != null && where.length > 0) {
			
			// Set the second level, a select op
			this.tree_root.children[0] = this.addOp(new OpSelect(where));
			
			// Set the third level
			this.tree_root.children[0].children[0] = this.addOp(new OpCrossProduct(table_names));
			
		} else {
			
			// Set the second level b/c there are no selections to make
			this.tree_root.children[0] = this.addOp(new OpCrossProduct(table_names));
			
		}
		
	}
	
	/**
	 * holds the algorithm that optimizes the query tree 
	 */
	public void optimize() {
		
		// TODO: write optimization algorithm
		
	}
	
	/**
	 * 
	 * @param op
	 * @return
	 */
	private Op addOp(Op op) {
		
		this.opList.add(op);
		return op;		
		
	}
	
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
		
		OpTree ot = new OpTree(table_names, fields, where);
		
		System.out.println(ot.toString());

	}

}
