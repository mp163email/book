package jdk_concurrent_package.threadpoll_calctime;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池统计时间
 * 1: 统计每个线程的耗时之和,实现Callable接口,将每个线程的耗时当返回值返回回来,最后循环用Future[]的get方法求和
 * 2：求所有线程的公共耗时,方法一：自定义线程池,重载销毁方法terminated,在那里求时间,这种方式要求必须要停掉线程池
 * 3：求所有线程的公共耗时,方法二：用CountDownLatch,在每个线程执行完任务countDown(), 通过它的await方法来求时间,这种方式不用停掉线程池
 * ***由下面的实验可得：在求公共耗时时,方法二要比方法一精确,应为方法一的时间里包含着线程池的初始化时间跟销毁时间***
 * @author mp
 * @date 2016-7-15 上午9:48:47
 */
public class ThreadPoolCalcTime {
	
	private static int nThread = 4;
	
	private static CountDownLatch cdl = new CountDownLatch(nThread);

	/**
	 * 任务一
	 * @author mp
	 * @date 2016-7-15 上午9:52:26
	 */
	public static class Task1 implements Callable<Long> {
		@Override
		public Long call() throws Exception {
			long start = System.currentTimeMillis();
			Random random = new Random();
			for (int i = 0; i < 100 * 10000; i++) {
				random.nextInt(100 * 10000);
			}
			long end = System.currentTimeMillis();
			long usedTime = (end - start);
			System.out.println(Thread.currentThread().getId() + " used time " + usedTime);
			cdl.countDown();
			return usedTime;
		}
	}
	
	/**
	 * 任务二
	 * @author mp
	 * @date 2016-7-15 上午10:15:38
	 */
	public static class Task2 implements Runnable {
		@Override
		public void run() {
			Random random = new Random();
			for (int i = 0; i < 100 * 10000; i++) {
				random.nextInt(100 * 10000);
			}
		}
	}
	
	/**
	 * 任务三
	 * @author mp
	 * @date 2016-7-15 上午10:24:16
	 */
	public static class Task3 implements Runnable {
		@Override
		public void run() {
			Random random = new Random();
			for (int i = 0; i < 100 * 10000; i++) {
				random.nextInt(100 * 10000);
			}
			cdl.countDown();
		}
	}
	
	/**
	 * 主函数
	 * @author mp
	 * @date 2016-7-15 上午9:54:58
	 * @param args
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 * @Description
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		
		System.out.println("一：统计每个线程的耗时之和,实现Callable接口,将每个线程的耗时当返回值返回回来,最后循环用Future[]的get方法求和");
		Task1 task1 = new Task1();
		Future<Long>[] fl = new Future [nThread];
		final long start1 = System.currentTimeMillis();
		ExecutorService exe1 = new ThreadPoolExecutor(nThread, nThread, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>()){
			@Override
			protected void terminated() {
				long end = System.currentTimeMillis();
				System.out.println("验证方式一公共耗时=" + (end - start1));
			}
		};
		long startTime1 = System.currentTimeMillis();
		for (int i = 0; i < nThread; i++) {
			fl [i] = exe1.submit(task1);
		}
		cdl.await();
		System.out.println("验证方式二公共耗时=" + (System.currentTimeMillis() - startTime1));
		exe1.shutdown();
		long totalTime = 0;
		for (int i = 0; i < fl.length; i++) {
			totalTime += fl[i].get();
		}
		System.out.println("total used time " + totalTime);
		TimeUnit.SECONDS.sleep(1);
		System.out.println();
		
		
		System.out.println("二：求所有线程的公共耗时,方法一：自定义线程池,重载销毁方法,在那里求时间");
		Task2 task2 = new Task2();
		final long start = System.currentTimeMillis();
		ExecutorService exe2 = new ThreadPoolExecutor(nThread, nThread, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>()){
			@Override
			protected void terminated() {
				long end = System.currentTimeMillis();
				System.out.println("方式一公共耗时=" + (end - start));
			}
		};
		for (int i = 0; i < nThread; i++) {
			exe2.execute(task2);
		}
		exe2.shutdown();
		TimeUnit.SECONDS.sleep(1);
		System.out.println();
		
		
		cdl = new CountDownLatch(nThread);
		System.out.println("三：用CountDownLatch,在每个线程执行完任务countDown(), 通过它的await方法来求时间,这种方式不用停掉线程池");
		Task3 task3 = new Task3();
		ExecutorService exe3 = Executors.newFixedThreadPool(nThread);
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < nThread; i++) {
			exe3.execute(task3);
		}
		cdl.await();
		long endTime = System.currentTimeMillis();
		System.out.println("方式二公共耗时=" + (endTime - startTime));
		exe3.shutdown();
	}
	
}
