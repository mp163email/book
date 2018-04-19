package thread_piped.piped_byte;

import java.io.PipedInputStream;

public class ReadThread extends Thread {
	
	private ReadData readData;
	
	private PipedInputStream inputStream;
	
	public ReadThread (ReadData readData, PipedInputStream inputStream) {
		this.readData = readData;
		this.inputStream = inputStream;
	}

	@Override
	public void run() {
		readData.readMethod(inputStream);
/*		while (true) {
			try {
				TimeUnit.SECONDS.sleep(1);
				System.out.println("read run");
			} catch (Exception e) {
			}
		}*/
	}
	
	public void ownMethod () {
		readData.readMethod(inputStream);
	}
	
}
