/**
 * 
 */
import java.util.*;

/**
 * @author chrisb This object iterates through the tuples in a given relation
 *         and returns the tuples through the getNext method
 */
public class Iterator {

	BufferManager bm = null;
	RelationInfo ri = null;

	Hashtable<String, Attribute> atts;
	int len;

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
	 * The number of the current block in the relation (1..x)
	 */
	int current_block_num;

	/**
	 * The number of tuples in the current block
	 */
	int num_tuples_in_block;

	/**
	 * The number of the current tuple in the block (1..x)
	 */
	int current_tuple_num;

	/**
	 * How many records are in the relation
	 */
	int records_in_relation;

	/**
	 * The record number in relation to the relation (1..x)
	 */
	int abs_record_number;

	/**
	 * Class constructor
	 * 
	 * @param bm
	 *            the BufferManager to use
	 * @param relation_id
	 *            the relation unique id #
	 * @param num_blocks
	 *            the number of blocks in this relation's file
	 */
	public Iterator(BufferManager bm, RelationInfo ri, int num_blocks) {

		this.ri = ri;
		this.bm = bm;
		this.relation_id = ri.getId();
		this.num_blocks = num_blocks;
		this.open();

	}

	/**
	 * Initialize the iterator with a relation
	 * 
	 * @param relation_id
	 *            the relation through which this iterator iterates
	 */
	public void open() {

		this.current_block_num = 0;
		this.current_tuple_num = 0;
		this.records_in_relation = Integer.parseInt(ri.getNumTuples().trim());
		this.abs_record_number = 0;

		atts = this.ri.getAttribute();

		// Calculate the total length of a tuple
		len = Utility.getTotalLength(atts);

		int blockOffset = this.current_block_num * Parameters.BLOCK_SIZE;
		this.current_block = bm.getBlock(Utility.combine(ri.getId(), blockOffset));
		int x = this.current_block.getRecordNumber();
		this.num_tuples_in_block = x;

		if (Debug.get().debug()) {

			System.out.println("INFO blockid:" + blockOffset + "(" + ri.getId() + "/" + blockOffset + ")");
			System.out.println("INFO: tuples in block: " + this.num_tuples_in_block);
			System.out.println("INFO: current tuple " + this.current_tuple_num);
			System.out.println("INFO: block " + this.current_block_num);
			if (this.current_block == null) System.out.println("INFO: block null");

		}

	}

	/**
	 * Returns the next tuple in the relation or null if no tuple exists.
	 * 
	 * @return the next Tuple
	 */
	public Tuple getNext() {

	    System.out.println("INFO: getNext()");
	    
		// Includes the tuple header
		int tuple_size = len;
		int offset;

		// The iterator is crossing a block boundary, so get the next block
		if (this.current_tuple_num >= this.num_tuples_in_block && this.abs_record_number < this.records_in_relation) {

			// Reset current tuple number
			this.current_tuple_num = 0;

			// Increment to get the next block
			this.current_block_num++;

			if (Debug.get().debug())System.out.println("INFO: current_block_number incremented");
			if (Debug.get().debug())System.out.println("INFO: current_tuple_number: " + this.current_tuple_num);

			// Get next block from the BufferManager
			int blockOffset = this.current_block_num * Parameters.BLOCK_SIZE;
			System.out.println("INFO: blockOffset: " + blockOffset);

			this.current_block = bm.getBlock(Utility.combine(this.relation_id, blockOffset));
			System.out.println("INFO: block retrieved");

			// If there is not another block, return null
			if (this.current_block != null) {

				// Set num_tuples from the Block we've just gotten
				this.num_tuples_in_block = this.current_block.getRecordNumber();
			}
		}

		if (Debug.get().debug()) System.out.println("INFO: tuple: " + (this.current_tuple_num + 1) + "/" + this.num_tuples_in_block);

		offset = (tuple_size * this.current_tuple_num) + Parameters.BLOCK_HEADER_SIZE;

		this.current_tuple_num++;
		this.abs_record_number++;

		if (this.current_block == null) {

			if (Debug.get().debug())System.out.println("INFO: iterator over; return null");

			return null;
		}

		if (Debug.get().debug()) System.out.println("INFO: tuple offset:" + offset + "/block offset:" + Utility.split(this.current_block.blockID)[1]	+ "/relationinfo:" + this.ri);

		return new Tuple(offset, this.current_block, this.ri);
	}

	/**
	 * Called to denote that this iterator is used and should not be reused
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
