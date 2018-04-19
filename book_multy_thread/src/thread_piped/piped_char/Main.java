package thread_piped.piped_char;

import java.io.PipedReader;
import java.io.PipedWriter;
import java.util.concurrent.TimeUnit;

public class Main {
	
	public static void main(String[] args) throws Exception {
		
		PipedReader inputStream = new PipedReader();
		PipedWriter outputStream = new PipedWriter();
		
		ReadData readData = new ReadData();
		WriteData writeData = new WriteData();
		
		inputStream.connect(outputStream);
//		outputStream.connect(inputStream);
		
		ReadThread readThread = new ReadThread(readData, inputStream);
		WriteThread writeThread = new WriteThread(writeData, outputStream);
		
		readThread.start();
//		writeThread.ownMethod();
		TimeUnit.SECONDS.sleep(3);
		writeThread.start();
//		readThread.ownMethod();
	}
	
}
