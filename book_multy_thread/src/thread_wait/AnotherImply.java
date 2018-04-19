package thread_wait;

/**
 * 在一个类中实现,更为直观
 * wait,notify,必须要有sync同步块,要先获得锁,否则执行会报异常
 * wait后会立刻释放锁
 * 而sleep,suspend则会一直占着锁
 * @author mp
 * @date 2016-7-12 下午5:12:40
 */
public class AnotherImply {
	
	public static Object lock = new Object();
	
	/**
	 * 等待线程类
	 * @author mp
	 * @date 2016-7-12 下午5:13:24
	 */
	public static class WaitThread extends Thread {
		@Override
		public void run() {
			synchronized (lock) {
				System.out.println(getName() + " wait before");
				try {
					lock.wait();//这个地方是对锁的wait,不是对当前线程的,所有在这个锁上的wait都会放到一个队列里
					System.out.println(getName() + " wait after");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 通知线程类
	 * @author mp
	 * @date 2016-7-12 下午5:16:26
	 */
	public static class NotifyThread extends Thread {
		@Override
		public void run() {
			synchronized (lock) {
				lock.notifyAll();//这里也是对某个锁的通知，通知这个锁所有等待队列里的线程
			}
		}
	}
	
	/**
	 * 主函数
	 * @author mp
	 * @date 2016-7-12 下午5:16:46
	 * @param args
	 * @throws InterruptedException 
	 * @Description
	 */
	public static void main(String[] args) throws InterruptedException {
		WaitThread w1 = new WaitThread();
		WaitThread w2 = new WaitThread();
		w1.start(); w2.start();
		Thread.sleep(5 * 1000);
		NotifyThread notify = new NotifyThread();
		notify.start();
	}
	
}
