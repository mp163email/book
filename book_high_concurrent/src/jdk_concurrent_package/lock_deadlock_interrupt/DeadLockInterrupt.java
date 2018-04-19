package jdk_concurrent_package.lock_deadlock_interrupt;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 用中断的方式,打破死锁
 * @author mp
 * @date 2016-7-11 下午3:32:53
 */
public class DeadLockInterrupt implements Runnable {

	public static ReentrantLock lock1 = new ReentrantLock();
	
	public static ReentrantLock lock2 = new ReentrantLock();
	
	int flag;
	
	public DeadLockInterrupt (int flag) {
		this.flag = flag;
	}
	
	@Override
	public void run() {
		try {
			if (flag == 1) {
				lock1.lockInterruptibly();//虽然持有锁,但能相应中断
				try {
					Thread.sleep(500);
				} catch (Exception e) {
				}
				lock2.lockInterruptibly();
			} else {
				lock2.lockInterruptibly();
				try {
					Thread.sleep(500);
				} catch (Exception e) {
				}
				lock1.lockInterruptibly();
			}
		} catch (Exception e) {
			System.out.println(Thread.currentThread().getName() + " 被中断");
			e.printStackTrace();
		} finally {
			if (lock1.isHeldByCurrentThread()) {
				lock1.unlock();
			}
			if (lock2.isHeldByCurrentThread()) {
				lock2.unlock();
			}
			System.out.println(Thread.currentThread().getName() + " 线程退出");
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		DeadLockInterrupt d1 = new DeadLockInterrupt(1);
		DeadLockInterrupt d2 = new DeadLockInterrupt(2);
		Thread thread1 = new Thread(d1);
		Thread thread2 = new Thread(d2);
		thread1.start(); thread2.start();
		Thread.sleep(5000);
		thread2.interrupt();//通过打断的方式,逃脱了死锁,但牺牲了一个线程作为代价
	}

}
