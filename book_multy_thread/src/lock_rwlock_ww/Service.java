package lock_rwlock_ww;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Service {
	private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	
	public void write1 () {
		try {
			lock.writeLock().lock();
			System.out.println(Thread.currentThread().getName() + "-------write1 sleep begin-------");
			Thread.sleep(1000*10);
			System.out.println(Thread.currentThread().getName() + "-------write1 sleep end-------");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	public void write2 () {
		try {
			lock.writeLock().lock();
			System.out.println(Thread.currentThread().getName() + "-------write2 sleep begin-------");
			Thread.sleep(1000*10);
			System.out.println(Thread.currentThread().getName() + "-------write2 sleep end-------");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.writeLock().unlock();
		}
	}
}
