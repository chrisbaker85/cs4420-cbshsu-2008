/**
 * 
 */

/**
 * @author chrisb
 * This object iterates through the tuples in a given relation
 * and returns the tuples through the getNext method
 */
public class Iterator {

	BufferManager bm = null;
	RelationInfo ri = null;
	
	/**
	 * the id of the relation
	 */
	int relation_id;
	
	/**
	 * The number of blocks in the relation
	 */
	int num_blocks;
	
	/**
	 * The current block in the relation
	 */
	Block current_block;
	
	/**
	 * The number of the current block in the relation
	 */
	int current_block_num;
	
	
	/**
	 * The number of tuples in the current block 
	 */
	int num_tuples;
	
	/**
	 * The number of the current tuple in the block
	 */
	int current_tuple_num;

	/**
	 * Class constructor
	 * @param bm the BufferManager to use
	 * @param relation_id the relation unique id #
	 * @param num_blocks the number of blocks in this relation's file
	 */
	public Iterator(BufferManager bm, RelationInfo ri, int relation_id, int num_blocks) {
		
		this.ri = ri;
		this.bm = bm;
		this.relation_id = ri.getId();
		this.num_blocks = num_blocks;
		this.open();
		
	}
	
	/**
	 * Initialize the iterator with a relation
	 * @param relation_id the relation through which this
	 * iterator iterates
	 */
	public void open() {
		
		this.current_block_num = 0;
		this.current_tuple_num = 0;
		
	}
	
	/**
	 * Returns the next tuple in the relation or null if no
	 * tuple exists.
	 * @return the next Tuple
	 */
	public Tuple getNext() {
		
		this.current_tuple_num++;
		
		// The iterator is crossing a block boundary, so get the next block
		if (this.current_tuple_num > this.num_tuples) {
		
			this.current_tuple_num = 0;
			
			// Increment to get the next block
			this.current_block_num++;
			// Get next block from the BufferManager
			this.current_block = bm.getBlock(Utility.combine(this.relation_id, this.current_block_num));

			// If there is not another block, return null
			if (this.current_block == null) {
				
				return null;
				
			} else {
				
				// Set num_tuples from the Block we've just gotten
				// TODO: fix me
				this.num_tuples = 0;
				
			}
			
		}
		
		// No new blocks were needed, so just return
		// the next tuple in the current block
		
		// TODO: fix me
		int header_size = 0;
		// TODO: fix me
		int tuple_size = 0;
		
		int offset = header_size + (tuple_size * this.current_tuple_num);
		
		return new Tuple(offset, this.current_block, this.ri);
		
	}
	
	/**
	 * Called to denote that this iterator is used and should
	 * not be reused 
	 */
	public void close() {
		
		this.bm = null;
		this.num_blocks = 0;
		this.relation_id = 0;
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {


	}

}
