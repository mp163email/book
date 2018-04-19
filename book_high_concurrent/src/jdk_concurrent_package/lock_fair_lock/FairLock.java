package jdk_concurrent_package.lock_fair_lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 公平锁
 * 优点：不会产生饥饿现象,比较公平
 * 缺点：需维护一个有序队列,成本高,性能低
 * @author mp
 * @date 2016-7-11 下午5:20:00
 */
public class FairLock implements Runnable {

	public ReentrantLock fairLock = new ReentrantLock(true);//读写锁有公平不公平设置, 原始的synchronized没有这个功能
	
	@Override
	public void run() {
		while (true) {
			try {
				fairLock.lock();
				System.out.println(Thread.currentThread().getName());
				try {
					TimeUnit.MILLISECONDS.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} finally {
				fairLock.unlock();
			}
		}
	}
	
	public static void main(String[] args) {
		FairLock fairLock = new FairLock();
		Thread t1 = new Thread(fairLock, "t1");
		Thread t2 = new Thread(fairLock, "t2");
		t1.start(); t2.start();
	}
	
}
