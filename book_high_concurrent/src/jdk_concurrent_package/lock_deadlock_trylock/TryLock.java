package jdk_concurrent_package.lock_deadlock_trylock;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用tryLock避免死锁
 * 原理：tryLock不会一直等待,而是立刻返回结果,得到锁返回true;没得到锁,返回false
 * @author mp
 * @date 2016-7-11 下午4:38:25
 */
public class TryLock implements Runnable {
	
	public static ReentrantLock lock1 = new ReentrantLock();
	
	public static ReentrantLock lock2 = new ReentrantLock();
	
	int lock;
	
	public TryLock (int lock) {
		this.lock = lock;
	}
	
	@Override
	public void run() {
		if (lock == 1) {
			while (true) {
				if (lock1.tryLock()) {
					try {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if (lock2.tryLock()) {
							try {
								System.out.println(Thread.currentThread().getName() + " : My Job done");
								return;
							} finally {
								lock2.unlock();
							}
						}
					} finally {
						lock1.unlock();
					}
				}
			}
		} else {
			while (true) {
				if (lock2.tryLock()) {
					try {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if (lock1.tryLock()) {
							try {
								System.out.println(Thread.currentThread().getName() + " : My Job done");
								return;
							} finally {
								lock1.unlock();
							}
						}
					} finally {
						lock2.unlock();
					}
				}
			}
		}
	}
	
	public static void main(String[] args) {
		Thread t1 = new Thread(new TryLock(1));
		Thread t2 = new Thread(new TryLock(2));
		t1.start(); t2.start();
	}
	
}
