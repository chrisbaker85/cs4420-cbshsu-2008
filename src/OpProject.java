import java.util.ArrayList;

/**
 * 
 */

/**
 * @author chrisb
 *
 */
public class OpProject extends Op {

	OpProject (ArrayList<String> attributes) {
		
		this.setType(this.opType.PROJECT);
		this.setContents(attributes);
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
