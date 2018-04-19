package thread_piped.piped_byte;

import java.io.PipedOutputStream;

public class WriteThread extends Thread {
	
	private WriteData writeData;
	
	private PipedOutputStream outputStream;
	
	public WriteThread (WriteData writeData, PipedOutputStream outputStream) {
		this.writeData = writeData;
		this.outputStream = outputStream;
	}

	@Override
	public void run() {
		writeData.writeMethod(outputStream);
/*		while (true) {
			try {
				TimeUnit.SECONDS.sleep(1);
				System.out.println("write run");
			} catch (Exception e) {
			}
		}*/
	}
	
	public void ownMethod () {
		writeData.writeMethod(outputStream);
	}
	
}
