package thread_catch_handler;

public class OneThreadMain {
	public static void main(String[] args) {
		
//		MyThread myThread1 = new MyThread();
//		myThread1.setUncaughtExceptionHandler(new CaughtExceptionHandler());
//		myThread1.start();
		
		MyThread myThread2 = new MyThread();
		myThread2.start();
	}
}
