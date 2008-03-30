/**
 * 
 */

/**
 * @author chrisb
 *
 */
public class OpCrossProduct extends Op {

	OpCrossProduct(String[] table_names) {
		
		this.setType(this.opType.CROSSPRODUCT);
		
		// Set # of children equal to number of tables
		this.children = new Op[table_names.length];
		
		// Set the children relations
		for (int i = 0; i < table_names.length; i++) {
		
			this.children[i] = new OpTable(table_names[i]);
			
		}
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
