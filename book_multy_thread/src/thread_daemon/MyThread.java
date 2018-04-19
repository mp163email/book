package thread_daemon;

public class MyThread extends Thread {
	private int i = 0;

	@Override
	public void run() {
		try {
			while (true) {
				if (Thread.interrupted()) {
					throw new Exception("interrupt");
				}
				i++;
				System.out.println(Thread.currentThread().getName() + "i=" + (i));
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
