/**
 * 
 */

/**
 * @author chrisb
 *
 */
public class OpJoin extends Op {

	OpJoin(String table1, String table2, String[] comparison, Op parent) {
		
		this.parent = parent;
		this.setType(this.opType.JOIN);
		this.contents = comparison;
		this.children = new Op[2];
		this.children[0] = new OpTable(table1, this);
		this.children[1] = new OpTable(table2, this);
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
