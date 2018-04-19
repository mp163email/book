package pairing_base.thread_priority;

/**
 * 线程优先级[不太准] --在竞争资源的时候,谁的优先级高,谁就能优先在排队队列中脱颖而出,得到资源
 * @author mp
 * @date 2016-7-11 上午11:42:12
 */
public class Thread_Priority {
	
	public static Object lock = new Object();
	
	/**
	 * 高优先级线程类
	 * @author mp
	 * @date 2016-7-11 上午11:47:42
	 */
	public static class HighPriority extends Thread {
		int count = 0;
		
		@Override
		public void run() {
			while (true) {
				synchronized (lock) {
					count ++;
					if (count > 10000000) {
						System.out.println("High Priority complete");
						break;
					}
				}
			}
		}
	}
	
	/**
	 * 中等优先级
	 * @author mp
	 * @date 2016-7-11 下午12:02:28
	 */
	public static class MiddlePriority extends Thread {
		int count = 0;
		
		@Override
		public void run() {
			while (true) {
				synchronized (lock) {
					count ++;
					if (count > 10000000) {
						System.out.println("MiddlePriority Priority complete");
						break;
					}
				}
			}
		}
	}
	
	/**
	 * 低优先级线程类
	 * @author mp
	 * @date 2016-7-11 上午11:47:54
	 */
	public static class LowPriority extends Thread {
		int count = 0;
		
		@Override
		public void run() {
			while (true) {
				synchronized (lock) {
					count ++;
					if (count > 10000000) {
						System.out.println("Low Priority complete");
						break;
					}
				}
			}
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		for (int i = 0; i < 10; i++) {
			Thread highThread = new HighPriority();
			highThread.setPriority(Thread.MAX_PRIORITY);
			highThread.start();
			
			Thread middleThread = new MiddlePriority();
			middleThread.setPriority(Thread.NORM_PRIORITY);
			middleThread.start();
			
			Thread lowThread = new LowPriority();
			lowThread.setPriority(Thread.MIN_PRIORITY);
			lowThread.start();
			
			highThread.join();
			middleThread.join();
			lowThread.join();
			
			System.out.println("=========");
		}
	}
	
}
