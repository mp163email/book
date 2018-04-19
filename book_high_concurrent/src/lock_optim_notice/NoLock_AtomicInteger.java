package lock_optim_notice;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 在java atomic包里有好多原子操作的工具类
 * 无锁的,原子的Integer
 * jdk在1.6后对synchronized优化了很多，很好了,测试结果竟然比无锁的原子的Integer还要快!!
 * @author mp
 * @date 2016-7-15 上午11:26:55
 */
public class NoLock_AtomicInteger {
	
	static AtomicInteger ai = new AtomicInteger(0);
	
	public static class Add implements Runnable {
		@Override
		public void run() {
			for (int i = 0; i < 100 * 10000; i++) {
				ai.incrementAndGet();
			}
		}
	}
	
	//-------------与同步自增做比较----------------
	
	static int num;
	
	synchronized static void add () {
		num ++;
	}
	
	public static class SyncAdd implements Runnable {
		
		@Override
		public void run() {
			for (int i = 0; i < 100 * 10000; i++) {
				add();
			}
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		long start = System.currentTimeMillis();
		Thread [] ts = new Thread[10];
		for (int i = 0; i < ts.length; i++) {
			ts[i] = new Thread(new Add());
		}
		for (int i = 0; i < ts.length; i++) {
			ts[i].start();
		}
		for (int i = 0; i < ts.length; i++) {
			ts[i].join();
		}
		long end = System.currentTimeMillis();
		System.out.println(ai.get() + " used time is " + (end - start));
		
		System.out.println("======================");
		ts = new Thread[10];
		long startMill = System.currentTimeMillis();
		SyncAdd syncAdd  = new SyncAdd();
		for (int i = 0; i < ts.length; i++) {
			ts[i] = new Thread(syncAdd);
		}
		for (int i = 0; i < ts.length; i++) {
			ts[i].start();
		}
		for (int i = 0; i < ts.length; i++) {
			ts[i].join();
		}
		long endMill = System.currentTimeMillis();
		System.out.println(NoLock_AtomicInteger.num + " used time is " + (endMill - startMill));
		
	}
	
}
