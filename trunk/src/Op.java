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
	public RelationInfo info;
	
	public Op parent;
	
	public OpType opType;
	
	public Object contents;
	
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
	
	public String toString() {
		
		String output = null;
		String contents = "";
		int children = ((this.children == null)?(0):(this.children.length));
		
		output = "|op:" + this.opType.name() + "\n" + "|contents: ";
		
		if (!(this.getContents() == null)) {
		
			if (this instanceof OpSelect) {
				
				String[][] comps = ((String[][])this.getContents());
				
				for (int i = 0; i < comps.length; i++) {
						
					output += "(" + comps[i][0] + " " + comps[i][1] + " " + comps[i][2] + ")";						
						
				}
				
			} else if (this instanceof OpProject) {
				
				for (int i = 0; i < ((String[])this.getContents()).length; i++) {
					
					output += "(" + ((String[])this.getContents())[i] + ")";
					
				}
				
			} else {
				
				output += this.getContents().toString();
				
			}
			
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
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
