/**
 * 
 */
package BufferManager;

/**
 * @author Sovandy
 *
 */
public class DataFile {

	/**
	 * 
	 */
	private String fileName;
	private int recordNumber;
	private int blockNumber;
	private int leafNode;
	
	public String getFileName()
	{
		return this.fileName;
	}
	
	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}
	
	public int getRecordNumber()
	{
		return recordNumber;
	}
	
	public void setRecordNumber(int recordNumber)
	{
		this.recordNumber = recordNumber;
	}
	
	public int getBlockNumber()
	{
		return this.blockNumber;
	}
	
	public void setBlockNumber(int blockNumber)
	{
		this.blockNumber = blockNumber;
	}
	
	public String getBlock(int blockNumber)
	{
		return "To be completed";
	}
	
	public void createFile(String fileName)
	{
	}
	
	public void insertRecord(String record)
	{
	}
	
	public String getRecord(int recordNumber)
	{
		return "to be completed";
	}
	public String getRecord(int blockNumber, int offset)
	{
		return "to be completed";
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	

}
