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
		

		// STEP ONE: FIND THE TABLES AND REMOVE THEM FROM THE XPROD
		
		String[] rem = new String[2];
		Op[] tables;
		OpJoin newJoin = null;
		
		rem[0] = Utility.getTable(cond[0]);
		rem[1] = Utility.getTable(cond[1]);
		
		if (Debug.get().debug()) System.out.println("INFO: tables to remove from xprod: " + rem[0] + "/" + rem[1]);
		// Remove the operations that have been marked
		tables = this.removeChildren(rem);
		
		String t = "";
		
		for (int i = 0; i < tables.length; i++) {
			
			t += "/" + tables[i].contents;
			
		}
		
		if (Debug.get().debug()) System.out.println("INFO: removed " + t);
		
		if (Debug.get().debug()) System.out.println("INFO: Removed " + tables.length + " table(s)");
		
		
		
		// STEP TWO: CREATE THE JOIN OPERATOR
		
		if (tables.length == 1) {
			
			// IF ONLY ONE TABLE IS REMOVED, CREATE A JOIN TO CHAIN
			newJoin = new OpJoin((OpJoin)this.children[0], tables[0], cond, this);

			// CHAIN THE JOIN
			this.children[0].parent = newJoin;
			this.swapChildren(this.children[0], newJoin);
			
		} else if (tables.length == 2) {
			
			// ELSE, CREATE A NEW JOIN
			newJoin = new OpJoin(rem[0], rem[1], cond, this);
			// JUST ADD THE JOIN TO THE XPROD, CAN'T CHAIN
			this.addChild(newJoin);
			
		}
		
		// STEP FOUR: REMOVE THE XPROD IF THERE IS ONE OP AND IT IS A JOIN
		if (this.children.length == 1 && (this.children[0] instanceof OpJoin)) {
			
			this.parent.swapChildren(this, this.children[0]);
			
		}
		
		
		
	}
	
	/**
	 * Relations (OpTable) should be removed from an OpCrossProduct if they're
	 * being added to a join op.
	 * @param rem the Op array of relations (OpTable) to remove
	 * @return the amended list of relations (OpTable)
	 */
	public Op[] removeChildren(String[] rem) {
		
		if (Debug.get().debug()) System.out.println("INFO: inside removeChildren.  Removing " + rem[0] + " and " + rem[1]);
		
		int num_to_remove = 0;
		
		// Find out how many tables to be removed are actual children
		// of the xprod
		for (int i = 0; i < this.children.length; i++) {
			
			for (int j = 0; j < rem.length; j++) {
				
				if (this.children[i] instanceof OpTable
						&& ((String)(this.children[i].contents)).equals(rem[j])) {
					
					num_to_remove++;
					
				}
				
			}
			
		}
		
		Op[] reduced = new Op[this.children.length - num_to_remove];
		int reduced_idx = 0;
		Op[] extract = new Op[num_to_remove];
		int extract_idx = 0;
		
		// go through each child and extract those given in parameter
		for (int i = 0; i < this.children.length; i++) {
			
			for (int j = 0; j < rem.length; j++) {
				
				if ((this.children[i] instanceof OpTable)
						&& ((OpTable)(this.children[i])).contents.equals(rem[j])) {
					
					System.out.println("INFO: " + ((OpTable)(this.children[i])).contents + " == " + rem[j]);
					System.out.println("INFO: extracted size: " + extract.length + "/index:" + extract_idx);
					
					// Add to the "extracted" array
					extract[extract_idx] = this.children[i];

					// Make the child null so that we know it's taken already 
					this.children[i] = null;
					extract_idx++;
					break;

				}
				
			}
			
		}
		
		// go through each child and pick out non-null children
		for (int i = 0; i < this.children.length; i++) {
			
			if (this.children[i] != null) {
				
				System.out.println("INFO: reduced size: " + reduced.length + "/index:" + reduced_idx);
				
				reduced[reduced_idx] = this.children[i];
				reduced_idx++;
				
			}
			
		}
		
		// Make this op's child list the reduced list
		this.children = reduced;
		
		// Return the extracted operations
		return extract;
		
	}

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		
	}

}
