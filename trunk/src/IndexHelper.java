/**
 * 
 */

/**
 * @author chrisb
 *
 */

/**
 * In fact, the only following commands are visible to the storage manager:
 * - insert: a key/pointer into an index file.
 * - getPointer: obtain the pointer associated with the given key.
 * 
 * With the given command:
 * - get pointer: first, the header of the index file will be read to obtain
 * the pointer to the root node. Read the root node. The index helper 
 * will determine how to travel the index file to search for a key 
 * and its corresponding pointer.
 * - insert: first, it calls the getPointer method to reach the leaf
 * that the new key/pointer pair should be inserted into. Then performs
 * the insertion.
 * 	 
 * 
 * You should implement an index scan which is an iterator that return all 
 * the key/pointer pairs of the index by travelling the leaf nodes. It
 * can be done easily by invoking the method getBlock. What you need
 * to do is to travel the right node of the current leaf node.
 * 
 * REMBEMBER: YOU DON'T BUILD A B+-TREE. Instead, you use the B+tree mechanism 
 * to travel the index file, create the links between the blocks in the index file,
 * and insert a key/pointer into a block. 
 *  
 * INDEX FILE STRUCTURE:
 * Reserve the first block of the file to contain the pointers
 * to the root node and the left-most node. All other blocks are either 
 * leaf nodes or internal nodes. 
 * The first byte should tell us how many keys are in the block.
 * The next byte contains the information if it is the leaf node or internal node.
 * The rest should be used to contain the keys and pointers.
 * The orer of the tree structure should be kept in a global configuration 
 * , i.e. parameter.java
 * 
 * BLOCK: each block contains m keys and (m+1) pointers. Therefore,
 * the first m*4 bytes are reserved for m keys (1 int occupies 4 bytes). 
 * The next (m+1)*4 bytes are used for pointers (either node pointer or 
 * block_num (data file) pointer).
 * 
 * NOTE:
 * Don't forget, the internal nodes have maximum m keys and maximum (m+1) pointers
 * to the child nodes.
 * The leaf nodes have maximum m keys and maximum m pointers to the block_num
 * of the data file that contains the tuples associated with the key. 
 * However, the leaf nodes also have a pointer to its immediate right node.
 * Therefore, the leaf nodes also have maximum (m+1) pointers where
 * the last pointer is used for referencing to its right leaf.
 */
public class IndexHelper {

	/**
	 * The B+-tree mechanisms to insert key/pointer or get pointer should
	 * be in insert/getPointer methods. Each block is considered as 
	 * tree node. You need to use the method getBlock() from
	 * the buffer manager to obtain the block information.
	 * Pls refer to the given B+-tree code for
	 * the detailed implementation.
	 * 
	 * YOU SHOULD implement the utility methods first before implementing
	 * insert, getPointer and getBlock methods.
	 */ 
	
	public boolean insert(int key, int pointer, IndexInfo info)
	{
		Block block = getBlock(key,info); //read the explanation for
										  //getBlock to know its meaning
		
		//The index file is empty
		if (block == null)
		{
			//- Create the root node. 
			//- Fill in the key/pointer information. 
			//- Root node is also a leaf node.
			//- Update the header (references to leaf node and root node).
			return false;
		}
		
		//If the index is not empty,
	
		//1. check if the node (block) is full.
		
		//2a. if not, fill the key/pair information into the leaf node. 
		//    (of course, don't forget to change the status of the block 
		//    to "updated").
		//   - Unpin the node
		//   - Return.
		
		//2b. otherwise, create a new node (block). Reorganize the old and new leaf nodes
		//with this new key/pointer information and existing keys/pointers in the leaf.
		
		//3. Read the parent node of the node we've just mentioned. Then,
		//  a/ Update the existing pointers in this node for the pointers that
		//  now point to the new node due to the reorganization of the keys
		//  in the child nodes.
		//  b/ Insert the new key/pointer into the parent node (since 
		//  the child node is splitted into 2 nodes, we have to create a
		//  new entry into the parent node).
		//The method to implement this actually the same as step 1, 2 and 3.		
		
		//Hint: insert key/pointer into an unfull node is straightforward.
		//The key thing is to create a new node. Sort the keys/pointers
		//of the node and the new key/pointer ascendingly.
		//Then divides them into two nodes. Select a key to represent
		//the new node and replace the presenter of the old node in the parent 
		//node by a new value.
		//
		//Of course, you now need to update the references in the parent node.
		//First, for every pointer in the parent node references to the keys in 
		//the old node which was moved to new node, UPDATE its reference.
		//
		//Now, insert the new presenter into the node. This may lead to
		//the block overfull and the new key will be propagated to the 
		//parent of the parent node until reaching the root node.
		//
		
		return false;
	}
	
	public int[] getPointer(int key, IndexInfo info)
	{
		Block block = getBlock(key,info);
		
		//1. Analyze the block to obtain the pointers associate with the key.
		//2a. If the block does not contain the key, return null.
		//2b. Otherwise, return the pointers.
		
		int[] temp = {0,1};
		return temp;
		
	}
	
	/**
	 * This method travel from the root node to the leaf that MAY contain
	 * or should contain the key and return the leaf.
	 * 
	 * I said "may" since we can use this method to reach the leaf node
	 * where we will insert a new key/pointer.
	 */ 
	private Block getBlock(int key, IndexInfo info)
	{
		//1. Read the index file header to obtain the root node pointer.
		
		//2a. if there is no root node, return null.		
		//2b. if there is, load the root node.
		
		//3. Analyze the node to determine the next node to follow.
		//Obtain the blockID of the next node.
		
		//4. Load the block with given blockID into buffer.
		
		//5. Goto step 3 until reach the leaf node.
		//6. Return the leaf node that the key should/may belong to.	
		
		//Hint: the key thing here is to determine which child of the node,
		//you should pick. It is simply the pointer between two keys A and B ,
		//where A < Key < B. If there is an equality, use the pointer associated
		//with equality.
		
		return new Block();
	}
	
	//The utility methods below provide a mechanism to manipulate the blocks.
	//It is straightforward. By the knowing the order of the tree,
	//you should be able compute the offset of the desired key (or pointer).
	//Next you can output the value of the key (or pointer) from this offset.
	//Or you can update the new key (or pointer) value.
	//
	//It should have nothing to do with B+-tree. THEREFORE:
	//
	//IMPLEMENT THESE METHODS FIRST before implement the insert and getPointer
	//method.
	/**
	 * Read the block and extract the keys. The blockID can be used
	 * to determine IndexInfo of the block.
	 */ 
	private int[] getKeys(Block block)
	{
		
		int[] temp = {0,1};
		return temp;
		
	}
		
	/**
	 * Read the block and extract all pointers.
	 */ 
	private int[] getPointers(Block block)
	{
		int[] temp = {0,1};
		return temp;
		
	}		
	
	/**
	 * Extract the ith key from the block.
	 */ 
	private int getKey(Block block, int i)
	{
		
		return 0;
	}
		
	/**
	 * Extract the ith pointer from the block.
	 */ 
	private int[] getPointers(Block block, int i)
	{
		
		int[] temp = {0,1};
		return temp;
		
	}	
	
	/**
	 * Put the value of the ith key into the block.
	 */ 
	private void putKey(Block block, int i, int key)
	{}
		
	/**
	 * Put the value of the ith pointer into the block.
	 */
	private void putPointer(Block block,int i, int pointer)
	{}	
		
	/**
	 * Get the pointer to the parent of the node.
	 */
	private int getParent(Block block)
	{
		
		return 0;
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
