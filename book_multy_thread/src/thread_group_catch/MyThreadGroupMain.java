package thread_group_catch;

public class MyThreadGroupMain {
	public static void main(String[] args) {
		MyThreadGroup threadGroup = new MyThreadGroup("我的线程组");
		MyThread[] myThreads = new MyThread[5];
		for (int i = 0; i < 5; i++) {
			myThreads[i] = new MyThread(threadGroup, (i + ""), "1");
			myThreads[i].start();
		}
		MyThread myThread = new MyThread(threadGroup, ("catch thread"), "a");
		myThread.start();
	}
}
