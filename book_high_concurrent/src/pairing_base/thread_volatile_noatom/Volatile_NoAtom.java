package pairing_base.thread_volatile_noatom;

/**
 * volatile可增强原子性,但不能保证原子性,能保证可见性,有序性
 * @author mp
 * @date 2016-7-11 上午11:19:16
 */
public class Volatile_NoAtom {

	volatile static int i = 0;
	
//	static Object lock = new Object();
	
	public static class Plush implements Runnable {

		@Override
		public void run() {
			for (int j = 0; j < 10000; j++) {
//				synchronized (lock) {
					i++;
//				}
			}
		}
	}

	public static void main(String[] args) throws InterruptedException {
		Thread[] threads = new Thread[10];
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new Thread(new Plush());
			threads[i].start();
		}
		
		//利用join方法,让所有对象线程都执行完,当前线程才会往下执行
		for (int i = 0; i < threads.length; i++) {
			threads[i].join();
		}
		
		System.out.println(i);
	}
	
}
