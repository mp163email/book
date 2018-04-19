package thread_state_timed_waiting;

import java.util.concurrent.TimeUnit;

public class MyThread extends Thread {
	
	public void run () {
		try {
			System.out.println("sleep begin");
			TimeUnit.SECONDS.sleep(10);
			System.out.println("sleep after");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
