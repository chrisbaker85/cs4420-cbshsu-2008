import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;


public class BTree
{
	public static int numberOfBlocks = 1 + (Parameters.BTREE_ORDER + 1) + (Parameters.BTREE_ORDER + 1)*(Parameters.BTREE_ORDER + 1);
	public static int sizeOfBlock = 8 * 2 + 8 * Parameters.BTREE_ORDER + 8 * (Parameters.BTREE_ORDER + 1);
	public static int sizeOfBuffer  = numberOfBlocks * sizeOfBlock;
	public static int blockCapacity  = 1 + Parameters.BTREE_ORDER;
	public static int rootBlockNum = 1;
	public static long rootPointer = 0;
	public static int firstInterBlockNum = 2;
	public static long firstInterBlockPointer = 2 * sizeOfBlock;
	public static int firstLeafBlockNum = 3 + Parameters.BTREE_ORDER;
	public static int firstLeafPointer = (3 + Parameters.BTREE_ORDER) * sizeOfBlock;
	
	protected MappedByteBuffer buffer;
	protected RandomAccessFile file;
		
	public BTree ()
	{
	}
	
	public BTree (String name)
	{
		try
		{
			file = new RandomAccessFile(name + ".index", "rw");
			FileChannel fileChannel = file.getChannel();
			buffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, sizeOfBuffer);
			buffer.load();
		} catch (FileNotFoundException e)
		{
		} catch (IOException e)
		{
		}
	}
	
	public static BTree readTree (String name)
	{
		BTree retVal = new BTree (); 
		try
		{
			retVal.file = new RandomAccessFile(name + ".index", "rw");
			FileChannel fileChannel = retVal.file.getChannel();
			retVal.buffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, sizeOfBuffer);
			retVal.buffer.load();
		} catch (FileNotFoundException e)
		{
		} catch (IOException e)
		{
		}
		return retVal;
	}
	public void update ()
	{
		buffer.force();
	}
	
	public long getBlock (int blockNum)
	{
		return (long)((blockNum - 1) * sizeOfBlock);
	}
	
	public void setKeysCount (int blockNum, int newCount)
	{
		buffer.putInt ((blockNum - 1) * sizeOfBlock, newCount);
	}
	
	public void setPointersCount (int blockNum, int newCount)
	{
		buffer.putInt ((blockNum - 1) * sizeOfBlock + 8, newCount);
	}
	
	public void setKey (int blockNum, int keyOrder, long newValue)
	{
		buffer.putLong ((blockNum - 1) * sizeOfBlock + 16 + (keyOrder - 1) * 8, newValue);
	}
	
	public void setPointer (int blockNum, int pointerOrder, long newValue)
	{
		buffer.putLong ((blockNum - 1) * sizeOfBlock + 16 + Parameters.BTREE_ORDER * 8 + (pointerOrder - 1) * 8, newValue);
	}
	
	public int keysCount (int blockNum)
	{
		return buffer.getInt ((blockNum - 1) * sizeOfBlock);
	}
	
	public int pointersCount (int blockNum)
	{
		return buffer.getInt ((blockNum - 1) * sizeOfBlock + 8);
	}
	
	public long getKey (int blockNum, int keyOrder)
	{
		return buffer.getLong ((blockNum - 1) * sizeOfBlock + 16 + (keyOrder - 1) * 8);
	}
	
	public long getPointer (int blockNum, int pointerOrder)
	{
		return buffer.getLong ((blockNum - 1) * sizeOfBlock + 16 + Parameters.BTREE_ORDER * 8 + (pointerOrder - 1) * 8);
	}
	
	public boolean isFull(int blockNum)
	{
		int keys = this.keysCount (blockNum);
		int ptrs = this.pointersCount (blockNum);
		
		if (blockNum == rootBlockNum)
		{
			return (ptrs == blockCapacity);
		}
		
		else if (blockNum >= firstInterBlockNum && blockNum < firstLeafBlockNum)
		{
			return (ptrs  == blockCapacity);
		}
		
		else
		{
			return (ptrs == blockCapacity);
		}
	}
	
	public boolean isEmpty(int blockNum)
	{
		int ptrs = this.pointersCount (blockNum);
		return (ptrs == 0);
	}
	
	public boolean hasLimit(int blockNum)
	{
		int keys = this.keysCount (blockNum);
		int ptrs = this.pointersCount (blockNum);
		
		if (blockNum == rootBlockNum)
		{
			return (ptrs >= 2);
		}
		
		else if (blockNum >= firstInterBlockNum && blockNum < firstLeafBlockNum)
		{
			return (ptrs >= Math.ceil(blockCapacity/2));
		}
		
		else
		{
			return (ptrs >= Math.floor(blockCapacity/2));
		}
	}
	
	private long pointTo(int blockNum, int keyOrder)
	{
		return (long)((blockNum - 1) * sizeOfBlock + 16 + (keyOrder - 1) * 8);
	}
			
	public static void main (String[] args)
	{
		BTree tree = new BTree("Test");
		tree.setPointersCount(1, 6);
		System.out.println (tree.isFull(1));
	}
	
}