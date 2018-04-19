package jdk_concurrent_package.locksupport;

import java.util.concurrent.locks.LockSupport;

/**
 * 线程阻塞工具类-测试响应中断
 * 和wait相比可以不用先获得锁,但在获得锁的情况下,跟suspend,sleep一样,阻塞时是独占锁不放
 * 但比suspend好的是,即便unpark在前,线程不会一直被挂起
 * @author mp
 * @date 2016-7-12 下午4:50:33
 */
public class LockSupportInterrupt {
	
	public static Object lock = new Object ();//假设有锁竞争
	
	static TestThread t1 = new TestThread("t1");
	
	static TestThread t2 = new TestThread("t2");
	
	/**
	 * 测试线程类
	 * @author mp
	 * @date 2016-7-12 下午4:54:03
	 */
	public static class TestThread extends Thread {
		
		public TestThread (String name) {
			super.setName(name);
		}

		@Override
		public void run() {
			synchronized (lock) {//放开注释,等待时会独占锁不放开
				System.out.println("in " + getName());
				LockSupport.park();//线程阻塞一直到unpark,这个时候是持有锁不放的,跟sleep,suspend一样自己独占锁
				if (Thread.interrupted()) {
					System.out.println(getName() + "被中断了");
				}
				System.out.println("out " + getName());
			}
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		t1.start();
		t2.start();
		Thread.sleep(1 * 1000);
		t1.interrupt();//中断t1
		Thread.sleep(3 * 1000);
		LockSupport.unpark(t2);//接触阻塞t2
	}
}
