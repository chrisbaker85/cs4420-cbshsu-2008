import java.util.ArrayList;

/**
 * 
 */

/**
 * @author chrisb
 *
 */
public class OpProject extends Op {

	OpProject (String[] attributes, Op parent, OpTree ot) {
		
		this.ot = ot;
		this.parent = parent;
		this.setType(this.opType.PROJECT);
		this.setContents(attributes);
		this.children = new Op[1];
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
