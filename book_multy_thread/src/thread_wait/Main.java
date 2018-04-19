package thread_wait;

public class Main {
	public static void main(String[] args) throws InterruptedException {
		
		Object lock = new Object();
		
		ThreadA threadA = new ThreadA(lock);
		threadA.start();
		
		ThreadB threadB = new ThreadB(lock);
		threadB.start();
		
		Thread.sleep(5 * 1000);
		
		new ThreadC(lock).start();
		
//		lock.notify();//直接调用会报错,   wait(),notify()都要在同步块中才生效,且wait()要try-catch
	}
}
