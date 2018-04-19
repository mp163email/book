package program.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class TestLock {
	
	ReentrantLock lock = new ReentrantLock();
	
	public static void main(String[] args)  throws Exception{
		new TestLock().start();
	}
	
	private void start() throws Exception {
		Thread first = new Thread(createTask(), "FirstThread");
		Thread second = new Thread(createTask(), "Second");
		first.start();
		second.start();
		Thread.sleep(600);
		second.interrupt();
		System.out.println("Main over");
	}
	
	private Runnable createTask () {
		return new Runnable() {
			
			@Override
			public void run() {
				try {
					lock();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
	}
	
	/**
	 * lock方式获得锁
	 * @author mp
	 * @date 2016-6-13 下午5:25:36
	 * @throws Exception
	 * @Description
	 */
	private void lock () throws Exception {
		while (true) {
			try {
				lock.lock();
				try {
					System.out.println("locked " + Thread.currentThread().getName());
					Thread.sleep(5000);
					System.out.println("sleep over " + Thread.currentThread().getName());
				} finally{
					lock.unlock();
					System.out.println("unlocked " + Thread.currentThread().getName());
				}
				break;
			} catch (Exception e) {
				System.out.println(Thread.currentThread().getName() + " is Interrupted ! ");
			}
		}
	}
	
	/**
	 * tryLock方式获得锁
	 * @author mp
	 * @date 2016-6-13 下午5:25:59
	 * @throws Exception
	 * @Description
	 */
	private void tryLock () throws Exception {
		while (true) {
			try {
				if (lock.tryLock()) {
					try {
						System.out.println("locked " + Thread.currentThread().getName());
						Thread.sleep(5000);
					} finally{
						lock.unlock();
						System.out.println("unlocked " + Thread.currentThread().getName());
					}
					break;
				} else {
					System.out.println("unable to lock " + Thread.currentThread().getName());
				}
			} catch (Exception e) {
				System.out.println(Thread.currentThread().getName() + " is Interrupted ! ");
			}
		}
	}
	
	/**
	 * tryLock指定时间方式获得锁
	 * @author mp
	 * @date 2016-6-13 下午5:26:25
	 * @param mill
	 * @throws Exception
	 * @Description
	 */
	private void tryLock (int mill) throws Exception {
		while (true) {
			try {
				if (lock.tryLock(500, TimeUnit.SECONDS)) {
					try {
						System.out.println("locked " + Thread.currentThread().getName());
						Thread.sleep(5000);
					} finally{
						lock.unlock();
						System.out.println("unlocked " + Thread.currentThread().getName());
					}
					break;
				} else {
					System.out.println("unable to lock " + Thread.currentThread().getName());
				}
			} catch (Exception e) {
				System.out.println(Thread.currentThread().getName() + " is Interrupted ! ");
			}
		}
	}
	
	/**
	 * lockInterruptibly方式获得锁
	 * @author mp
	 * @date 2016-6-13 下午5:27:56
	 * @param mill
	 * @throws Exception
	 * @Description
	 */
	private void lockInterruptibly (int mill) throws Exception {
		while (true) {
			try {
				lock.lockInterruptibly();
				try {
					System.out.println("locked " + Thread.currentThread().getName());
					Thread.sleep(5000);
				} finally{
					lock.unlock();
					System.out.println("unlocked " + Thread.currentThread().getName());
				}
				break;
			} catch (Exception e) {
				System.out.println(Thread.currentThread().getName() + " is Interrupted ! ");
			}
		}
	}
	
}
