package thread_group_allstop;

public class MyThread extends Thread {

	public MyThread (ThreadGroup group, String name) {
		super(group, name);
	}

	@Override
	public void run() {
		try {
			System.out.println("Start Thread Name=" + Thread.currentThread().getName());
			while (true) {
				if (Thread.interrupted()) {
					throw new Exception();
				}
			}
		} catch (Exception e) {
			System.out.println("End Thread Name=" + Thread.currentThread().getName());
		}
	}
	
}
