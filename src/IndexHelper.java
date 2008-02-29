import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class IndexHelper {
	public static int numberOfBlocks = 1 + (Parameters.BTREE_ORDER + 1)
			+ (Parameters.BTREE_ORDER + 1) * (Parameters.BTREE_ORDER + 1);
	public static int sizeOfBlock = 8 * 2 + 8 * Parameters.BTREE_ORDER + 8
			* (Parameters.BTREE_ORDER + 1);
	public static int sizeOfBuffer = numberOfBlocks * sizeOfBlock;
	public static int blockCapacity = 1 + Parameters.BTREE_ORDER;
	public static int rootBlockNum = 1;
	public static long rootPointer = 0;
	public static int firstInterBlockNum = 2;
	public static long firstInterBlockPointer = 2 * sizeOfBlock;
	public static int firstLeafBlockNum = 3 + Parameters.BTREE_ORDER;
	public static int firstLeafPointer = (3 + Parameters.BTREE_ORDER)
			* sizeOfBlock;

	protected MappedByteBuffer buffer;
	protected RandomAccessFile file;

	public IndexHelper() {
	}

	public IndexHelper(String name) {
		try {
			file = new RandomAccessFile(name + ".index", "rw");
			FileChannel fileChannel = file.getChannel();
			buffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0,
					sizeOfBuffer);
			buffer.load();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}

	public static IndexHelper readTree(String name) {
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

	public void update() {
		buffer.force();
	}

	public long getBlock(int blockNum) {
		return (long) ((blockNum - 1) * sizeOfBlock);
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