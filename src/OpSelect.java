import java.util.ArrayList;

/**
 * 
 */

/**
 * @author chrisb
 *
 */
public class OpSelect extends Op {

	OpSelect(String[][] where_clause, Op parent, OpTree ot) {
		
		this.ot = ot;
		this.parent = parent;
		this.setType(Op.OpType.SELECT);
		this.setContents(where_clause);
		this.children = new Op[1];
		
	}
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
