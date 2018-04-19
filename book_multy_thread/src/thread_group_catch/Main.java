package thread_group_catch;

public class Main {
	public static void main(String[] args) {
		ThreadGroup threadGroup = new ThreadGroup("我的线程组");
		for (int i = 0; i < 5; i++) {
			MyThread myThread = new MyThread(threadGroup, (i + ""), "1");
			myThread.start();
		}
		MyThread myThread = new MyThread(threadGroup, ("1"), "a");
		myThread.start();
	}
}
