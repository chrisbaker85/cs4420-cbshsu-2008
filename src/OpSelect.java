import java.util.ArrayList;

/**
 * 
 */

/**
 * @author chrisb
 *
 */
public class OpSelect extends Op {

	OpSelect(ArrayList list) {
		
		this.setType(Op.OpType.SELECT);
		this.setContents(list);
		
	}
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
