/**
 * @author chrisb
 *
 */
public class Block {

	public byte[] content;
	
	/**
	 * With isPin, isUpdated and blockID inside this class, 
	 * the address allocation table is unnecessary.
	 */		
	public boolean isPinned = false;
	
	/**
	 * The blockID to which this block reference.  The first
	 * 4 bytes of the variable is the file number.  the second
	 * 4 bytes is the offset of the block in the file on disk.
	 */ 
	public long blockID = -1;
	
	/**
	 * This flag should be set to true when a transaction updates the block 
	 * and writes it to the buffer.
	 */
	private boolean isUpdated = false;
	
	Block() {
		
		// Dim content to right size
		this.content = new byte[Parameters.BLOCK_SIZE];
		
		// Fill the first 4 bytes with for creation of new block
		byte [] header = Utility.makeByte4FromInt(0);
		content[0] = header[0];
		content[1] = header[1];
		content[2] = header[2];
		content[3] = header[3];
		
	}
	
	/**
	 * Use this under normal circumstances
	 * Calls the generic constructor
	 * @param blockID
	 * @param content
	 */
	public Block(long blockID, byte[] content) {
		
		// Call default constructor to initialize variables
		this();
		
		// Set the block ID
		this.blockID = blockID;
		
		// Write the given data to the content variable
		this.writeToBlock(content);
		
	}
	
	/**
	 * it returns the number of record in the block. 
	 * @return
	 */
	public int getRecordNumber()
	{
		byte [] byteArray = new byte[4];
		byteArray[0] = this.content[0];
		byteArray[1] = this.content[1];
		byteArray[2] = this.content[2];
		byteArray[3] = this.content[3];
		if (Debug.get().debug())System.out.println ("getRecordNumber");
		if (Debug.get().debug())System.out.println ("byteArray[0]: " + byteArray[0]);
		if (Debug.get().debug())System.out.println ("byteArray[1]: " + byteArray[1]);
		if (Debug.get().debug())System.out.println ("byteArray[2]: " + byteArray[2]);
		if (Debug.get().debug())System.out.println ("byteArray[3]: " + byteArray[3]);
		return Utility.makeIntFromByte4(byteArray);
	}
	
	/**
	 * It updates the number of records in the block
	 */
	public void updateRecordNumber(int numRecords)
	{
		// Get the number of records in the block already
		byte [] byteArray = new byte[4];
		byteArray[0] = this.content[0];
		byteArray[1] = this.content[1];
		byteArray[2] = this.content[2];
		byteArray[3] = this.content[3];
		if (Debug.get().debug())System.out.println ("updateRecordNumber");
		if (Debug.get().debug())System.out.println ("byteArray[0]: " + byteArray[0]);
		if (Debug.get().debug())System.out.println ("byteArray[1]: " + byteArray[1]);
		if (Debug.get().debug())System.out.println ("byteArray[2]: " + byteArray[2]);
		if (Debug.get().debug())System.out.println ("byteArray[3]: " + byteArray[3]);
		int recNum = Utility.makeIntFromByte4(byteArray);
		if (Debug.get().debug())if (Debug.get().debug())System.out.println ("recNum: " + recNum);
		
		// Add to the number of records that is in the block now
		recNum = recNum + numRecords;
		if (Debug.get().debug())if (Debug.get().debug())System.out.println ("recNum: " + recNum);
		byteArray = Utility.makeByte4FromInt(recNum);
		if (Debug.get().debug())System.out.println ("byteArray[0]: " + byteArray[0]);
		if (Debug.get().debug())System.out.println ("byteArray[1]: " + byteArray[1]);
		if (Debug.get().debug())System.out.println ("byteArray[2]: " + byteArray[2]);
		if (Debug.get().debug())System.out.println ("byteArray[3]: " + byteArray[3]);
		
		// Set the new number back
		content[0] = byteArray[0];
		content[1] = byteArray[1];
		content[2] = byteArray[2];
		content[3] = byteArray[3];
		
	}

	public int getBlockNumber()
	{
		int[] temp = Utility.split(this.blockID);
		return temp[1];
	}
	
	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public boolean isPinned() {
		return isPinned;
	}

	public void setPinned(boolean isPinned) {
		this.isPinned = isPinned;
	}

	public long getBlockID() {
		return blockID;
	}

	public void setBlockID(long blockID) {
		this.blockID = blockID;
	}

	public boolean isUpdated() {
		return isUpdated;
	}

	public void setUpdated(boolean isUpdated) {
		this.isUpdated = isUpdated;
	}
	
	/**
	 * append data to the block that has free space for it to be write it back to file
	 * It will be used by insert query
	 * @param data
	 */
	public void writeToBlock(byte[] data)
	{
		/*
		byte [] test = {data[4], data[5], data[6], data[7]}; 
		String temp = new String(test);
		System.out.println("first name in writeToBlock should be " + temp);
		*/
		
		this.isUpdated = true;
		this.isPinned = true;
		int recNum = this.getRecordNumber();
		//System.out.println("INFO: #records: " + recNum);
		int appendPos = data.length * recNum + Parameters.BLOCK_HEADER_SIZE;
		if (data.length == 1024) appendPos = 0;
		//System.out.println("[" + data.length + "/" + recNum + "/" + Parameters.BLOCK_HEADER_SIZE + "]");
		//System.out.println("POS: " + pos);
		
		// Append the new data to the end of the existing data
		for (int i = 0; i < data.length; i++)
		{
			this.content[appendPos+i] = data[i];
		}
		
		if (data.length < Parameters.BLOCK_SIZE) this.updateRecordNumber(1);
		//System.out.println(this.getRecordNumber());
		this.isPinned = false;
	}
	
	/**
	 * return array of byte inside the block
	 * @param offset
	 * @param len
	 * @return
	 */
	public byte [] getTupleContent(int offset, int len)
	{
		byte [] data = new byte[len];
		
		for (int i = 0; i < len; i++)
		{
			data[i] = content[offset + i];
		}
		return data;
	}
	
	public void printBlock ()
	{
		System.out.println ("ID: " + this.blockID);
		System.out.println ("Updated: " + this.isUpdated);
		System.out.println ("pinned: " + this.isPinned);
		for (int i = 0; i < Parameters.BLOCK_SIZE; i++) {
			System.out.print (" Byte # " + i + ": " + (char) content[i]);
			if (i % 128 == 0)
			{
				System.out.println();
			}
		}
	}
}
