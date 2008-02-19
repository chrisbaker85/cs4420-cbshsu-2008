import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class WriteTestDriver {

	public static void main(String[] args) {
		try {
			int start = 0;
			int size = Parameters.BLOCK_SIZE;

			byte[] tempCap = new byte[Parameters.BLOCK_SIZE / 2];
			byte[] tempReg = new byte[Parameters.BLOCK_SIZE / 2];

			int i;

			for (i = 0; i < Parameters.BLOCK_SIZE / 2; i++) {
				tempCap[i] = (byte) ((i % 26) + 65);
			}

			for (i = 0; i < Parameters.BLOCK_SIZE / 2; i++) {
				tempReg[i] = (byte) ((i % 26) + 97);
			}

			RandomAccessFile fileOut = new RandomAccessFile("test.txt", "rw");
			FileChannel fileChannel = fileOut.getChannel();
			MappedByteBuffer buffer = fileChannel.map(
					FileChannel.MapMode.READ_WRITE, start, size);

			buffer.put(tempCap);
			buffer.put(tempReg);

			for (i = 0; i < 100; i++) {
				buffer.put(512 + i, (byte) ' ');
			}
			fileOut.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
