package thread_state_waiting;

/**
 * wait, join
 * @author mp
 * @date 2016-5-25 下午5:32:24
 */
public class Main {
	public static void main(String[] args) throws Exception {
		MyThread myThread = new MyThread();

		Thread threadA = new Thread(myThread);
		threadA.start();
		
		Thread.sleep(1000);
		
		System.out.println("main方法中threadA的状态=" + threadA.getState());
	}
}
