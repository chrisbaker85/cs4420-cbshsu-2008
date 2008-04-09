import java.util.ArrayList; 

/**
 * @authors Quoc Minh Nguyen, chrisb
 *
 */
public abstract class Op {

	/* 
	 * Contains the statistics and attributes infomation of
	 * the output relation. For example: a cross product will
	 * output a new relation tempR from two relation R1(a,b), R2(c,d) .The 
	 * info will contain the attribute (a,b,c,d)  and statistic (T(R), B(R), V(R,a),
	 * V(R,b), V(R,c), V(R,d)).
	 * information for tempR(a,b,c,d).
	 */
	protected RelationInfo info;
	
	/**
	 * The parent to this operator
	 */
	protected Op parent;
	
	/**
	 * The operator type.  See OpType below
	 */
	protected OpType opType;
	
	/**
	 * The unique operator ID
	 */
	protected int id;
	
	/**
	 * The contents of the operator: a String[], or String[][]
	 */
	protected Object contents;
	
	/**
	 * Helps in query plan generation...I think
	 */
	private boolean used = false;
	
	/**
	 * In a logical query plan, the op can contain more than 2 children so that
	 * it is easier to perform the tree transformation during the optimization process.
	 * However, in a physical query plan, there are only two children.
	 */ 
	public Op[] children;
	
	public enum OpType {SELECT, PROJECT, CROSSPRODUCT, JOIN, TABLE, SORT_BASED_JOIN, INDEX_BASED_JOIN, SORT}
	
	public Op left() {return children[0];}
	
	public Op right() {return children[1];}
	
	/** 
	 * Obtain the output relation info from child nodes. Including the statistics.
	 * The implementation depends on node type.
	 */ 
	public RelationInfo getInfo() {
		
		return this.info;
		
	}
	
	public void setType(OpType type) {
		
		this.opType = type;
		
	}
	
	public OpType getType() {
		
		return this.opType;
		
	}
	
	public Object getContents() {
		
		return this.contents;
		
	}
	
	public void setContents(Object o) {
		
		this.contents = o;
		
	}
	
	/**
	 * Tells if the op has been used in query plan generation
	 * @return
	 */
	public boolean isUsed() {
		
		return this.used;
		
	}
	
	/**
	 * Allows stating that this op has been "used" in query plan generation
	 */
	public void use() {
		
		this.used = true;
		
	}
	
	public String toString() {
		
		String output = null;
		String contents = "";
		int children = ((this.children == null)?(0):(this.children.length));
		
		output = "|op:" + this.opType.name() + "\n" + "|contents: ";
		
		if (!(this.getContents() == null)) {
		
			if (this instanceof OpSelect) {
				
				String[] comp = ((String[])this.getContents());
				
						
				output += "(" + comp[0] + " " + comp[1] + " " + comp[2] + ")";						
						
				
			} else if (this instanceof OpProject) {
				
				for (int i = 0; i < ((String[])this.getContents()).length; i++) {
					
					output += "(" + ((String[])this.getContents())[i] + ")";
					
				}
				
			} else {
				
				output += this.getContents().toString();
				
			}
			
		}
		
		if (this.info != null) {
			
			output += "\n|relationinfo set";
			
		}
		
		output += "\n|children:" + children + "\n\n";
		
		
		if (this.children != null) {
		for (int i = 0; i < this.children.length; i++) {

			output += this.children[i].toString();
			
		}
		}
		
		return output;
		
	}
	
	/**
	 * Checks to see if this operator has any children that have
	 * not yet been "visited" by the query plan algorithm
	 * @return true or false
	 */
	public boolean hasUnvisitedChildren() {
		
		boolean value = false;
		
		if (this.children != null) {
		
			for (int i = 0; i < this.children.length; i++) {
				
				if (!this.children[i].isUsed()) {
					
					value = true;
					
				}
				
			}
			
		}
		
		return value;
		
	}
	
	/**
	 * Sets the operator's UID
	 * @param id int (0, 1, 2, ...)
	 */
	public void setID(int id) {
		
		this.id = id;
		
	}
	
	/**
	 * Gets the operator's UID
	 * @return int ID
	 */
	public int getID() {
		
		return this.id;
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
