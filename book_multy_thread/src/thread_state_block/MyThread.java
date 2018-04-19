package thread_state_block;

import java.util.concurrent.TimeUnit;

public class MyThread extends Thread {
	
	Object object = new Object();
	
	public void run () {
		try {
			synchronized (object) {
				System.out.println("sleep begin");
				TimeUnit.SECONDS.sleep(10);
				System.out.println("sleep after");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
