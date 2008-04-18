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
	 * A reference to the OpTree for adding/removing operations
	 */
	protected OpTree ot = null;
	
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
	
	public String getString() {
		
		String output = null;
		String contents = "";
		String children = "";
		String parent;
		
		if (this.parent == null) parent = null;
		else parent = this + (new Integer(this.parent.id)).toString();
		
		
		output = "|op: (" + this.id + ")\n|parent:" + this.parent + "\n|info: " + this.info + "\n|contents: ";
		
		if (!(this.getContents() == null)) {
		
			if (this instanceof OpSelect) {
				
				String[] comp = ((String[])this.getContents());
				
						
				output += "(" + comp[0] + " " + comp[1] + " " + comp[2] + ")";						
						
				
			} else if (this instanceof OpProject) {
				
				for (int i = 0; i < ((String[])this.getContents()).length; i++) {
					
					output += "(" + ((String[])this.getContents())[i] + ")";
					
				}
				
			} else if (this instanceof OpJoin) {
				
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

		if (this.children != null) {
		
			for (int i = 0; i < this.children.length; i++) {
				
				children += "(" + this.children[i] + ")";
				
			}
			
		}
		
		
		output += "\n|children:" + children + "\n\n";
		
		
		if (this.children != null) {
		for (int i = 0; i < this.children.length; i++) {

			output += this.children[i].getString();
			
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
	
   public boolean hasChildren(String table_1, String table_2) {
        
       boolean e1 = false;
       boolean e2 = false;
       
        if (this.children != null) {
            
            for (int i = 0; i < this.children.length; i++) {
                
                if (this.children[i].contents.equals(table_1)) e1 = true;
                if (this.children[i].contents.equals(table_2)) e2 = true;
                
            }
            
        }
        
        return (e1 && e2);
        
    }
   
   /**
    * Check to see if this operator's subtree contains the given table
    * @param table_name the table name we're looking for
    * @return an int >= 0
    */
   public int containsTable(String table_name) {
	   
	   int retVal = 0;
	   
	   // Base case, this is the table we're looking for
	   if (this instanceof OpTable
			   && ((String)this.contents).equals(table_name)) {
		   if (Debug.get().debug()) System.out.println("INFO: table " + table_name + "found");
		   return 1;
		   
	   }
	   
	   if (this.children != null) {
		   // Loop through every child
		   for (int i = 0; i < this.children.length; i++) {
			   
			   retVal += this.children[i].containsTable(table_name);
			   
		   }
	   }

	   
	   return retVal;
	   
   }

   /**
    * Swaps out an operator for another
    * Useful when pushing operators up and down
    * @param oldChild the old operator to be replaced
    * @param newChild the new operator to replace the old
    */
   public void swapChildren(Op oldChild, Op newChild) {
	   
	   for (int i = 0; i < this.children.length; i++) {
		   
		   if (this.children[i] == oldChild) {
			   
			   this.children[i] = newChild;
			   
			   newChild.parent = this;
			   
			   if (Debug.get().debug()) System.out.println("INFO: swapped [" + oldChild + "(" + oldChild.getType() + ")] with [" + newChild + "(" + newChild.getType() + ")]");
			   //ot.opList.remove(oldChild);
			   
		   }
		   
	   }
	   
	   
   }
   
   /**
    * Inject newChild and push down oldChild
    * @param newChild
    * @param oldChild
    */
   public void injectAboveChild(Op newChild, Op oldChild) {
	   
	   for (int i = 0; i < this.children.length; i++) {
		   
		   if (this.children[i] == oldChild) {
			   
			   this.swapChildren(oldChild, newChild);
			   
			   newChild.children[0] = oldChild;
			   oldChild.parent = newChild;
			   
			   break;
			   
		   }
		   
	   }
	   
   }
   
   public Op[] removeChildren(String[] table) {
       
       if (this instanceof OpCrossProduct) {
       
           Op[] ops = new Op[2];
           
           for (int i = 0; i < this.children.length; i++) {
               
               if (this.children[i].contents.equals(Utility.getTable(table[0]))) {
                   
                   ops[0] = this.children[i];
                   
               }
               
               if (this.children[i].contents.equals(Utility.getTable(table[1]))) {
                   
                   ops[1] = this.children[i];
               }
               
           }
           
           return ops;
       }
       
       return null;
       
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
	
	public void addChild(Op newChild) {
		
		Op[] children = new Op[this.children.length + 1];
		
		for (int i = 0; i < this.children.length; i++) {
			
			children[i] = this.children[i];
			
		}
		
		children[children.length - 1] = newChild;
		
		this.children = children;
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
