package lock_getHoldCount;

import java.util.concurrent.locks.ReentrantLock;

public class Service {
	public ReentrantLock lock = new ReentrantLock(false);//true-
	
	public void method () {
		try {
			lock.lock();
			System.out.println(Thread.currentThread().getName() + "------调用lock的次数为-----" + lock.getHoldCount());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
}
