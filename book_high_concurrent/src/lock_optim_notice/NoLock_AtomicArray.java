package lock_optim_notice;

import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * 无锁数组使用
 * @author mp
 * @date 2016-7-18 下午2:12:59
 */
public class NoLock_AtomicArray {
	
	/**
	 * 原子的线程安全的数组
	 */
	private static AtomicIntegerArray array = new AtomicIntegerArray(10);
	
	/**
	 * 对10个下标做1万次累加
	 * @author mp
	 * @date 2016-7-18 下午2:15:14
	 */
	public static class AddThread implements Runnable {
		@Override
		public void run() {
			for (int i = 0; i < 10 * 10000; i++) {//用这种方式很有意思
				array.getAndIncrement(i % array.length());//为每个下标累加多少次
			}
		}
	}
	
	/**
	 * 主函数
	 * @author mp
	 * @date 2016-7-18 下午2:18:56
	 * @param args
	 * @throws InterruptedException
	 * @Description
	 */
	public static void main(String[] args) throws InterruptedException {
		Thread [] ts = new Thread[10];
		for (int i = 0; i < ts.length; i++) {
			ts[i] = new Thread(new AddThread());
		}
		for (int i = 0; i < ts.length; i++) {
			ts[i].start();
		}
		for (int i = 0; i < ts.length; i++) {
			ts[i].join();
		}
		System.out.println(array);
	}
}
