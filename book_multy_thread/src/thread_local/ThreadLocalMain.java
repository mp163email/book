package thread_local;

public class ThreadLocalMain {
	public static void main(String[] args) {
		ThreadA threadA = new ThreadA();
		ThreadB threadB = new ThreadB();
		threadA.start();
		threadB.start();
		
		for (int i = 0; i < 10; i++) {
			Tools.t1.set(i);
			System.out.println("main = " + Tools.t1.get());
		}
	}
}
