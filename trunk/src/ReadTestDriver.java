import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class ReadTestDriver {

	public static void main(String[] args) {
		try {
			int start = 0;

			RandomAccessFile fileIn = new RandomAccessFile("test.txt", "rw");
			FileChannel fileChannel = fileIn.getChannel();
			MappedByteBuffer buffer = fileChannel.map(
					FileChannel.MapMode.READ_WRITE, start, fileIn.length());
			
			fileChannel.read(buffer);
		
		    int i = 0;
		    int offSet = 100;
		    
		    for (i = 0; i < 10; i ++)
		    {
		      byte b = buffer.get(i + offSet);
		      System.out.println( "Character "+ (i + offSet) + ": "+((char)b) );
		    }
		    			
			fileIn.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
