package thread_state_block;

/**
 * 等待获得索的时候
 * @author mp
 * @date 2016-5-25 下午5:32:43
 */
public class Main {
	public static void main(String[] args) throws Exception {
		MyThread myThread = new MyThread();

		Thread threadA = new Thread(myThread);
		threadA.start();
		
		Thread.sleep(1000);
		
		Thread threadB = new Thread(myThread);
		threadB.start();
		
		System.out.println("main方法中threadA的状态=" + threadA.getState());
		System.out.println("main方法中threadB的状态=" + threadB.getState());
	}
}
