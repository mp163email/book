package lock_rwlock_rr;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Service {
	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	
	public void read () {
		try {
			lock.readLock().lock();
			System.out.println(Thread.currentThread().getName() + "-------read sleep begin-------");
			Thread.sleep(1000*10);
			System.out.println(Thread.currentThread().getName() + "-------read sleep end-------");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.readLock().unlock();
		}
	}
}
