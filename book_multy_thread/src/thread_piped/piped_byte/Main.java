package thread_piped.piped_byte;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.concurrent.TimeUnit;

public class Main {
	
	public static void main(String[] args) throws Exception {
		
//		PipedInputStream inputStream = new PipedInputStream(5000);//构造函数里可以修改初始buffer的大小
		PipedInputStream inputStream = new PipedInputStream();
		PipedOutputStream outputStream = new PipedOutputStream();
		
		ReadData readData = new ReadData();
		WriteData writeData = new WriteData();
		
		inputStream.connect(outputStream);
//		outputStream.connect(inputStream);
		
		ReadThread readThread = new ReadThread(readData, inputStream);
		WriteThread writeThread = new WriteThread(writeData, outputStream);
		
		writeThread.start();
		TimeUnit.SECONDS.sleep(3);
		readThread.start();
//		writeThread.ownMethod();
//		readThread.ownMethod();
	}
	
}
