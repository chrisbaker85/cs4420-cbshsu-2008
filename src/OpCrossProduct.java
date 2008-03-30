/**
 * 
 */

/**
 * @author chrisb
 *
 */
public class OpCrossProduct extends Op {

	OpCrossProduct(String table1, String table2) {
		
		this.setType(this.opType.CROSSPRODUCT);
		this.children = new Op[2];
		this.children[0] = new OpTable(table1);
		this.children[1] = new OpTable(table2);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
