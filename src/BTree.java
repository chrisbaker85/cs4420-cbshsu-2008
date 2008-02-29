import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;


public class BTree
{
	public static int numberOfNodes = 1 + (Parameters.BTREE_ORDER + 1) + (Parameters.BTREE_ORDER + 1)*(Parameters.BTREE_ORDER + 1);
	public static int sizeOfNode = 8 * 2 + 8 * Parameters.BTREE_ORDER + 8 * (Parameters.BTREE_ORDER + 1);
	public static int sizeOfBuffer  = numberOfNodes * sizeOfNode;
	public static int nodeCapacity  = 1 + Parameters.BTREE_ORDER;
	public static int rootNodeRank = 1;
	public static int firstInterNodeRank = 2;
	public static int firstLeafNodeRank = 3 + Parameters.BTREE_ORDER;
	
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
	
	public void setKeysCount (int rank, int newCount)
	{
		buffer.putInt ((rank - 1) * sizeOfNode, newCount);
	}
	
	public void setPointersCount (int rank, int newCount)
	{
		buffer.putInt ((rank - 1) * sizeOfNode + 8, newCount);
	}
	
	public void setKey (int rank, int keyOrder, int newValue)
	{
		buffer.putInt ((rank - 1) * sizeOfNode + 16 + (keyOrder - 1) * 8, newValue);
	}
	
	public void setPointer (int rank, int pointerOrder, long newValue)
	{
		buffer.putLong ((rank - 1) * sizeOfNode + 16 + Parameters.BTREE_ORDER * 8 + (pointerOrder - 1) * 8, newValue);
	}
	
	public int keysCount (int rank)
	{
		return buffer.getInt ((rank - 1) * sizeOfNode);
	}
	
	public int pointersCount (int rank)
	{
		return buffer.getInt ((rank - 1) * sizeOfNode + 8);
	}
	
	public int getKey (int rank, int keyOrder)
	{
		return buffer.getInt ((rank - 1) * sizeOfNode + 16 + (keyOrder - 1) * 8);
	}
	
	public long getPointer (int rank, int pointerOrder)
	{
		return buffer.getLong ((rank - 1) * sizeOfNode + 16 + Parameters.BTREE_ORDER * 8 + (pointerOrder - 1) * 8);
	}
	
	public boolean isFull(int rank)
	{
		int keys = this.keysCount (rank);
		int ptrs = this.pointersCount (rank);
		
		if (rank == rootNodeRank)
		{
			return (ptrs == nodeCapacity);
		}
		
		else if (rank >= firstInterNodeRank && rank < firstLeafNodeRank)
		{
			return (ptrs  == nodeCapacity);
		}
		
		else
		{
			return (ptrs == nodeCapacity);
		}
	}
	
	public boolean hasLimit(int rank)
	{
		int keys = this.keysCount (rank);
		int ptrs = this.pointersCount (rank);
		
		if (rank == rootNodeRank)
		{
			return (ptrs >= 2);
		}
		
		else if (rank >= firstInterNodeRank && rank < firstLeafNodeRank)
		{
			return (ptrs >= Math.ceil(nodeCapacity/2));
		}
		
		else
		{
			return (ptrs >= Math.floor(nodeCapacity/2));
		}
	}
			
	public static void main (String[] args)
	{
		BTree tree = new BTree("Test");
		System.out.println (tree.buffer.getLong(16));
	}
	
}