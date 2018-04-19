package thread_state_timed_waiting;

/**
 * sleep(mill), wait(mill), join(mill)
 * @author mp
 * @date 2016-5-25 下午5:33:04
 */
public class Main {
	public static void main(String[] args) throws Exception {
		MyThread myThread = new MyThread();
		myThread.start();
		Thread.sleep(1000);
		System.out.println("main方法中这个线程的状态=" + myThread.getState());
	}
}
