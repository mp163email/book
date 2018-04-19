package thread_catch_handler;

public class ManyThreadMain {
	public static void main(String[] args) {
		
		MyThread.setDefaultUncaughtExceptionHandler(new CaughtExceptionHandler());
		
		MyThread myThread1 = new MyThread();
		myThread1.start();
		
		MyThread myThread2 = new MyThread();
		myThread2.start();
	}
}
