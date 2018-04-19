package thread_local;

public class ThreadB extends Thread {

	@Override
	public void run() {
		for (int i = 0; i < 10; i++) {
			Tools.t1.set(i);
			System.out.println("ThreadB = " + Tools.t1.get());//只能取放一个值
		}
	}
	
}
