package lock_fair;

import java.util.concurrent.locks.ReentrantLock;

public class Service {
	
	private ReentrantLock lock = null;
	
	public Service (boolean isFair) {
		lock = new ReentrantLock();
	}
	
	public void method () {
		try {
			lock.lock();
			System.out.println(Thread.currentThread().getName() + "获得锁");
		} catch (Exception e) {
			e.printStackTrace();
		} finally { 
			lock.unlock();
		}
	}
	
}
