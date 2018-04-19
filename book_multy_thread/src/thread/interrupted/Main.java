package thread.interrupted;

import java.util.concurrent.TimeUnit;


public class Main {
	public static void main(String[] args) throws Exception{
		MyThread myThread = new MyThread();
		myThread.start();
		TimeUnit.MILLISECONDS.sleep(10);
		myThread.interrupt();
	}
}
