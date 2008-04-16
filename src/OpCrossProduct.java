/**
 * 
 */

/**
 * @author chrisb
 *
 */
public class OpCrossProduct extends Op {

	OpCrossProduct(String[] table_names, Op parent) {
		
		this.parent = parent;
		this.setType(this.opType.CROSSPRODUCT);
		
		// Set # of children equal to number of tables
		this.children = new Op[table_names.length];
		
		// Set the children relations
		for (int i = 0; i < table_names.length; i++) {
		
			this.children[i] = new OpTable(table_names[i], this);
			
		}
		
	}
	
	/**
	 * Relations (OpTable) should be removed from an OpCrossProduct if they're
	 * being added to a join op.
	 * @param rem the Op array of relations (OpTable) to remove
	 * @return the amended list of relations (OpTable)
	 */
	public void removeChildren(Op[] rem) {
		
		Op[] red = new Op[this.children.length - rem.length];
		Op[] ext = new Op[rem.length];
		   
		for (int i = 0; i < this.children.length; i++) {
			
//			if (this.children[i]) {
//				
//				
//			}
			
		}
		
		this.children = red;
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
