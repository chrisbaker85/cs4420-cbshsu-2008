/**
 * 
 */

/**
 * @author chrisb
 *
 */
public class OpSort extends Op {

	OpSort(Op parent, OpTree ot) {
		
		this.ot = ot;
		this.parent = parent;
		this.setType(this.opType.SORT);
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
