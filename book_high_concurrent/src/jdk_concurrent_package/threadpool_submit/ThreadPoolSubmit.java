package jdk_concurrent_package.threadpool_submit;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 线程池submit提交
 * 可以接受Callable参数
 * 带返回值
 * ?响应异常?
 * @author mp
 * @date 2016-7-15 上午9:48:47
 */
public class ThreadPoolSubmit {

	/**
	 * 随机数任务,实现Callable接口,带返回值, throws异常
	 * @author mp
	 * @date 2016-7-15 上午9:52:26
	 */
	public static class Task implements Callable<Long> {

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
			return usedTime;
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
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		Task task = new Task();
		Future<Long>[] fl = new Future [4];
		ExecutorService exe = Executors.newFixedThreadPool(4);
		for (int i = 0; i < 4; i++) {
			fl [i] = exe.submit(task);
		}
		long totalTime = 0;
		for (int i = 0; i < fl.length; i++) {
			totalTime += fl[i].get();
		}
		System.out.println("total used time " + totalTime);
	}
	
}
