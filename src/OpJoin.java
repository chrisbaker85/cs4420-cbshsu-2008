/**
 * 
 */

/**
 * @author chrisb
 *
 */
public class OpJoin extends Op {

	OpJoin(String table1, String table2, String[] comparison, Op parent, OpTree ot) {
		
		this.ot = ot;
		this.parent = parent;
		this.setType(this.opType.JOIN);
		this.contents = comparison;
		this.children = new Op[2];
		this.children[0] = ot.addOp(new OpTable(table1, this, ot));
		this.children[1] = ot.addOp(new OpTable(table2, this, ot));
		ot.opList.add(this.children[0]);
		ot.opList.add(this.children[1]);
		
	}
	
	OpJoin(OpJoin leftChild, Op table2, String[] comp, Op parent, OpTree ot) {
		
		this.ot = ot;
		this.parent = parent;
		this.setType(this.opType.JOIN);
		this.contents = comp;
		this.children = new Op[2];
		this.children[0] = leftChild;
		this.children[1] = table2;
		table2.parent = this;
		leftChild.parent = this;
		
	}
	
	/**
	 * Helps assembly of joins from cross product
	 * @param relationName the relation to look for in the join
	 * @return true if this join contains the relation passed in
	 */
	public boolean containsRelation(String relationName) {
		
		// Check the children if they are OpJoins
		// to see if one of them contains the relation
		for (int i = 0; i < this.children.length; i++) {

			if (this.children[i] instanceof OpJoin
					&& ((OpJoin)this.children[i]).containsRelation(relationName)) {
				return true;
			}
			
		}

		
		// Check the left side of the condition for relation name
		if (((String[])this.contents)[0].contains(relationName)) {
			return true;
		}

		// Check the right side of the condition for relation name
		if (((String[])this.contents)[1].contains(relationName)) {
			return true;
		}
		
		// If the relation name cannot be found, quit
		
		return false;
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
