package jdk_concurrent_package.threadpool_catch;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 执行者-Main函数
 * @author mp
 * @date 2016-7-13 下午3:23:27
 */
public class ExecutorMain {

	/**
	 * 自定义除法任务
	 * @author mp
	 * @date 2016-7-13 下午3:25:31
	 */
	public static class Task implements Runnable {

		private int a;
		
		private int b;
		
		public Task (int a, int b) {
			this.a = a;
			this.b = b;
		}
		
		@Override
		public void run() {
			System.out.println((a / b));
			try {
				TimeUnit.SECONDS.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 主函数
	 * @author mp
	 * @date 2016-7-13 下午3:32:07
	 * @param args
	 * @Description
	 */
	public static void main(String[] args) {
		ThreadPoolExecutor myexec = new TraceThreadPool(5, 5, 0, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<Runnable>());
		for (int i = 0; i < 5; i++) {
			Task task = new Task(10, i);
			myexec.execute(task);
		}
		myexec.shutdown();
	}
	
}
