/**
 * 
 */

/**
 * @author chrisb
 *
 */
public class OpTable extends Op {

	OpTable (String tablename) {
		
		this.setType(this.opType.TABLE);
		this.setContents(tablename);
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
