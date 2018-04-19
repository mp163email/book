package thread_state_waiting;


public class MyThread extends Thread {
	
	Object lock = new Object();
	
	public void run () {
		try {
			synchronized (lock) {
				System.out.println("wait begin");
				lock.wait();
				System.out.println("wait after");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
