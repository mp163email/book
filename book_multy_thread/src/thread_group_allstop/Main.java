package thread_group_allstop;

public class Main {
	public static void main(String[] args) {
		try {
			ThreadGroup group = new ThreadGroup("My Thread Group");
			for (int i = 0; i < 5; i++) {
				MyThread myThread = new MyThread(group, "Thread" + (i + 1));
				myThread.start();
			}
			Thread.sleep(5000);
			group.interrupt();//组被打断,组内所有线程都被打扰
			System.out.println("main group interrupt");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
