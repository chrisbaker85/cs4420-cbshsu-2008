/**
 * 
 */

/**
 * @author chrisb
 *
 */
public class OpIndexBasedJoin extends Op {

	OpIndexBasedJoin(String table1, String table2, String[] comparison, Op parent, OpTree ot) {
		
		this.ot = ot;
		this.parent = parent;
		this.setType(this.opType.INDEX_BASED_JOIN);
		this.contents = comparison;
		this.children = new Op[2];
		this.children[0] = new OpTable(table1, this, ot);
		this.children[1] = new OpTable(table2, this, ot);
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
