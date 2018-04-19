package thread_piped.piped_char;

import java.io.PipedWriter;

public class WriteThread extends Thread {
	
	private WriteData writeData;
	
	private PipedWriter outputStream;
	
	public WriteThread (WriteData writeData, PipedWriter outputStream) {
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
