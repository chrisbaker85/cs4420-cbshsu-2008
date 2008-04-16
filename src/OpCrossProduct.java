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
	 * Function that adds a join operator to the xprod op.
	 * 
	 * @param cond a String[] containing the join table condition
	 */
	public void addJoin(String[] cond) {
		
		String[] rem = new String[2];
		Op[] tables;
		
		rem[0] = Utility.getTable(cond[0]);
		rem[1] = Utility.getTable(cond[1]);
		
		tables = this.removeChildren(rem);
		
		if (this.children.length > 1) {
			
			// Find an existing OpJoin to add the new OpJoin to
			for (int i = 0; i < this.children.length; i++) {
				
				// Determine if this particular child is the right one to use
				if (this.children[i] instanceof OpJoin && (((OpJoin)this.children[i]).containsRelation(rem[0]) || ((OpJoin)this.children[i]).containsRelation(rem[1]))) {
					
					
					
					
					
				}
				
			}
			
		} else {
			
			// If there is only one child, then remove the xprod
			
		}
		

		
		
	}
	
	/**
	 * Relations (OpTable) should be removed from an OpCrossProduct if they're
	 * being added to a join op.
	 * @param rem the Op array of relations (OpTable) to remove
	 * @return the amended list of relations (OpTable)
	 */
	public Op[] removeChildren(String[] rem) {
		
		Op[] reduced = new Op[this.children.length - rem.length];
		int reduced_idx = 0;
		Op[] extract = new Op[rem.length];
		int extract_idx = 0;
		
		// go through each child and put into proper array
		for (int i = 0; i < this.children.length; i++) {
			
			for (int j = 0; j < rem.length; j++) {
				
				if (((OpTable)(this.children[i])).contents.equals(rem[j])) {
					
					reduced[reduced_idx] = this.children[i];
					reduced_idx++;
					
				} else {
					
					extract[extract_idx] = this.children[i];
					extract_idx++;
				}
				
			}
			
		}
		
		this.children = reduced;
		return extract;
		
	}

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		
	}

}
