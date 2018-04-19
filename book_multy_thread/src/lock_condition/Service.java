package lock_condition;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Service {
	
	private Lock lock = new ReentrantLock();
	
	private Condition condition = lock.newCondition();
	
	public void wait_method () {
		try {
			lock.lock();
			System.out.println(Thread.currentThread().getName() + " wait before");
			condition.await();
			System.out.println(Thread.currentThread().getName() + " wait after");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
	
	public void signal_method () {
		try {
			lock.lock();
			System.out.println(Thread.currentThread().getName() + " signal");
			condition.signalAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
}
