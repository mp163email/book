package thread_join;

public class ThreadX extends Thread {

	@Override
	public void run() {
		try {
			int second = (int)(Math.random() * 10000);
			Thread.sleep(second);
			System.out.println(Thread.currentThread().getName() + " 线程结束");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
