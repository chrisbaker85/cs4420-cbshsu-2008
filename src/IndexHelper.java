import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class IndexHelper {
	
	public static int numberOfBlocks = 1 + (Parameters.BTREE_ORDER + 1)	+ (Parameters.BTREE_ORDER + 1) * (Parameters.BTREE_ORDER + 1);
	public static int sizeOfBlockHeader = 8 * 4;
	public static int sizeOfBlockKeys = 8 * Parameters.BTREE_ORDER;
	public static int sizeOfBlockPointers = 8 * (Parameters.BTREE_ORDER + 1);
	public static int sizeOfBlock = sizeOfBlockHeader +  sizeOfBlockKeys + sizeOfBlockPointers;
	public static int sizeOfIndexFile = numberOfBlocks * sizeOfBlock;
	public static int blockCapacity = 1 + Parameters.BTREE_ORDER;
	
	
	public int rootBlockNum;
	public int firstLeafBlockNum;

	protected MappedByteBuffer buffer;
	protected RandomAccessFile file;

	public IndexHelper() {
		rootBlockNum = -1;
		firstLeafBlockNum = -1;
	}

	public IndexHelper(String name) {
		rootBlockNum = -1;
		firstLeafBlockNum = -1;
		try {
			file = new RandomAccessFile(name + ".index", "rw");
			FileChannel fileChannel = file.getChannel();
			buffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0,
					sizeOfBuffer);
			buffer.putInt (rootBlockNum);
			buffer.putInt (firstLeafBlockNum);
			buffer.load();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}

	public static IndexHelper readIndexFile(String name) {
		IndexHelper retVal = new IndexHelper();
		try {
			retVal.file = new RandomAccessFile(name + ".index", "rw");
			FileChannel fileChannel = retVal.file.getChannel();
			retVal.buffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0,
					sizeOfBuffer);
			retVal.buffer.load();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
		return retVal;
	}
	
	public int getRootNum ()
	{
		return buffer.getInt (0);
	}
	
	public int getLeafNum ()
	{
		return buffer.getInt (16);
	}

	public void update() {
		buffer.force();
	}

	public long getBlock(int blockNum) {
		return (long) (2 * 8 + (blockNum - 1) * sizeOfBlock);
	}

	public long[] getPointers(int blockNum, long keyValue) {
		long[] retVal = null;

		int keyOrder = getKeyOrder(blockNum, keyValue);

		if (keyOrder != -1) {
			retVal = new long[2];
			retVal[0] = getLeftPointer(blockNum, keyOrder);
			retVal[1] = getLeftPointer(blockNum, keyOrder);
		}
		return retVal;
	}

	public void setKeysCount(int blockNum, int newCount) {
		buffer.putInt((blockNum - 1) * sizeOfBlock, newCount);
	}

	public void setPointersCount(int blockNum, int newCount) {
		buffer.putInt((blockNum - 1) * sizeOfBlock + 8, newCount);
	}

	public void setKey(int blockNum, int keyOrder, long newValue) {
		buffer.putLong((blockNum - 1) * sizeOfBlock + 16 + (keyOrder - 1) * 8,
				newValue);
	}

	public void placeKey(int blockNum, long newValue) {
		int place = 1;

		while (place <= Parameters.BTREE_ORDER) {
			if (0 == getKey(blockNum, place)) {
				setKey(blockNum, place, newValue);
				break;
			}
			place++;
		}
	}

	public void setPointer(int blockNum, int pointerOrder, long newValue) {
		buffer
				.putLong((blockNum - 1) * sizeOfBlock + 16
						+ Parameters.BTREE_ORDER * 8 + (pointerOrder - 1) * 8,
						newValue);
	}

	public int keysCount(int blockNum) {
		return buffer.getInt((blockNum - 1) * sizeOfBlock);
	}

	public int pointersCount(int blockNum) {
		return buffer.getInt((blockNum - 1) * sizeOfBlock + 8);
	}

	public long getKey(int blockNum, int keyOrder) {
		return buffer.getLong((blockNum - 1) * sizeOfBlock + 16
				+ (keyOrder - 1) * 8);
	}

	public long getKey(long pointer) {
		return buffer.getLong((int) pointer);
	}

	public boolean hasKey(int blockNum, long keyValue) {
		int i = 1;
		boolean retVal = false;

		while (i <= Parameters.BTREE_ORDER) {
			if (keyValue == getKey(blockNum, i)) {
				retVal = true;
				break;
			}
			i++;
		}

		return retVal;
	}

	public int getKeyOrder(int blockNum, long keyValue) {
		int i = 1;
		int retVal = -1;

		while (i <= Parameters.BTREE_ORDER) {
			if (keyValue == getKey(blockNum, i)) {
				retVal = i;
				break;
			}
			i++;
		}

		return retVal;
	}

	public long getPointer(int blockNum, int pointerOrder) {
		return buffer.getLong((blockNum - 1) * sizeOfBlock + 16
				+ Parameters.BTREE_ORDER * 8 + (pointerOrder - 1) * 8);
	}

	public long getLeftPointer(int blockNum, int keyOrder) {
		return (long) ((blockNum - 1) * sizeOfBlock + 16
				+ Parameters.BTREE_ORDER * 8 + (keyOrder - 1) * 8);
	}

	public long getReightPointer(int blockNum, int keyOrder) {
		return (long) ((blockNum - 1) * sizeOfBlock + 16
				+ Parameters.BTREE_ORDER * 8 + (keyOrder - 1) * 8 + 8);
	}

	public long getLeftPointer(int blockNum, long keyValue) {
		int keyOrder = getKeyOrder(blockNum, keyValue);
		return (long) ((blockNum - 1) * sizeOfBlock + 16
				+ Parameters.BTREE_ORDER * 8 + (keyOrder - 1) * 8);
	}

	public long getReightPointer(int blockNum, long keyValue) {
		int keyOrder = getKeyOrder(blockNum, keyValue);
		return (long) ((blockNum - 1) * sizeOfBlock + 16
				+ Parameters.BTREE_ORDER * 8 + (keyOrder - 1) * 8 + 8);
	}

	public long pointToKey(int blockNum, int keyOrder) {
		return (long) ((blockNum - 1) * sizeOfBlock + 16 + (keyOrder - 1) * 8);
	}

	public boolean isFull(int blockNum) {
		int keys = this.keysCount(blockNum);
		int ptrs = this.pointersCount(blockNum);

		if (blockNum == rootBlockNum) {
			return (ptrs == blockCapacity);
		}

		else if (blockNum >= firstInterBlockNum && blockNum < firstLeafBlockNum) {
			return (ptrs == blockCapacity);
		}

		else {
			return (ptrs == blockCapacity);
		}
	}

	public boolean isEmpty(int blockNum) {
		int ptrs = this.pointersCount(blockNum);
		return (ptrs == 0);
	}

	public boolean hasLimit(int blockNum) {
		int keys = this.keysCount(blockNum);
		int ptrs = this.pointersCount(blockNum);

		if (blockNum == rootBlockNum) {
			return (ptrs >= 2);
		}

		else if (blockNum >= firstInterBlockNum && blockNum < firstLeafBlockNum) {
			return (ptrs >= Math.ceil(blockCapacity / 2));
		}

		else {
			return (ptrs >= Math.floor(blockCapacity / 2));
		}
	}

	public void printPointer(long pointerValue) {

		int blockNum = (int) (pointerValue / sizeOfBlock) + 1;

		System.out.print("(" + blockNum + ", " + this.getKey(pointerValue)
				+ ")");

	}

	public void printBlock(int blockNum) {
		System.out.println("Block Munmber: " + blockNum);

		if (blockNum == rootBlockNum) {
			System.out.println("Block Type: Root");
		}

		else if (blockNum >= firstInterBlockNum && blockNum < firstLeafBlockNum) {
			System.out.println("Block Type: Inter");
		}

		else {
			System.out.println("Block Type: Leaf");
		}

		System.out.println("Keys Count " + keysCount(blockNum));
		System.out.println("Pointers Count " + pointersCount(blockNum));

		for (int i = 1; i <= Parameters.BTREE_ORDER; i++) {
			System.out.print("Key " + i + ": " + this.getKey(blockNum, i)
					+ "\t");
		}

		System.out.println();

		for (int i = 1; i <= Parameters.BTREE_ORDER + 1; i++) {
			System.out.print("Pointer " + i + ": ");

			if (blockNum < firstLeafBlockNum) {
				printPointer(getPointer(blockNum, i));
			} else {
				if (i == Parameters.BTREE_ORDER + 1) {
					printPointer(getPointer(blockNum, i));
				}

				else {
					System.out.print("(" + getPointer(blockNum, i) + ")");
				}
			}

			System.out.print("\t");
		}
		System.out.println();
		System.out.println();

	}

	public void printIndexFile(boolean includeEmpty) {

		for (int i = 1; i <= numberOfBlocks; i++) {

			if (includeEmpty) {
				printBlock(i);
			}

			else if (i == 1) {
				printBlock(i);
			} else if (!isEmpty(i)) {
				printBlock(i);
			}

		}
	}

	public static void main(String[] args) {
		IndexHelper test = new IndexHelper("Test");
		test.printIndexFile(true);
	}

}