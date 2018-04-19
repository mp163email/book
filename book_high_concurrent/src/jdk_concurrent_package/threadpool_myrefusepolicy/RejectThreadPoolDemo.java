package jdk_concurrent_package.threadpool_myrefusepolicy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 自定义拒绝策略
 * @author mp
 * @date 2016-7-13 上午11:23:28
 */
public class RejectThreadPoolDemo {
	
	/**
	 * 要执行的任务
	 * @author mp
	 * @date 2016-7-13 上午11:25:15
	 */
	public static class MyTask implements Runnable {
		
		 private int id;
		 
		 public MyTask (int id) {
			 this.id = id;
		 }
		 
		 public int getId () {
			 return id;
		 }
		
		@Override
		public void run() {
			System.out.println(System.currentTimeMillis() + ": Thread ID:" + Thread.currentThread().getId());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 主函数
	 * @author mp
	 * @date 2016-7-13 上午11:25:31
	 * @param args
	 * @Description
	 */
	public static void main(String[] args) throws Exception{
		
		//自定义固定线程数的线程池
		ExecutorService es = new ThreadPoolExecutor(5, 5, 0L, TimeUnit.MILLISECONDS,
			//指定长度的无界队列<>泛型里要指定类型,Runnable否则会编译不通过
			new LinkedBlockingDeque<Runnable>(10), Executors.defaultThreadFactory(), 
			//自定义的拒绝策略,实现了RejectedExecutionHandler接口
			new RejectedExecutionHandler() {
				@Override
				public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
					MyTask myTask = (MyTask)r;
					System.out.println("taskId=" + myTask.getId() + " is discard");
				}
			}
		);
		
		for (int i = 0; i < 30; i++) {
			MyTask task = new MyTask(i);
			es.execute(task);
			Thread.sleep(10);
		}
		es.shutdown();
	}
	
}
