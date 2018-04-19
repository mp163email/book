package jdk_concurrent_package.threadpool_extend;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 扩展线程池 -监控任务执行耗时
 * @author mp
 * @date 2016-7-13 上午11:23:28
 */
public class ThreadPoolExtendDemo {
	
	/**
	 * 要执行的任务
	 * @author mp
	 * @date 2016-7-13 上午11:25:15
	 */
	public static class MyTask implements Runnable {
		
		private int id;
		 
		private long startExecTime;

		public long getStartExecTime() {
			return startExecTime;
		}

		public void setStartExecTime(long startExecTime) {
			this.startExecTime = startExecTime;
		}

		/**
		 * 构造方法
		 * @param id
		 * @param execTime
		 */
		public MyTask (int id) {
			 this.id = id;
		 }
		 
		 public int getId () {
			 return id;
		 }
		
		@Override
		public void run() {
			System.out.println(Thread.currentThread().getName() + " is executing task-" + getId());
			try {
				if (id % 2 == 0) {
					Thread.sleep(1 * 1000);
				} else {
					Thread.sleep(3 * 1000);
				}
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
			new LinkedBlockingDeque<Runnable>(10), Executors.defaultThreadFactory()
		){
			/**
			 * 开始调用时触发
			 */
			@Override
			protected void beforeExecute(Thread t, Runnable r) {
				//设置任务开始执行时间
				MyTask myTask = (MyTask)r;
				myTask.setStartExecTime(System.currentTimeMillis());
			}

			/**
			 * 调用完成后触发
			 */
			@Override
			protected void afterExecute(Runnable r, Throwable t) {
				long currMill = System.currentTimeMillis();
				MyTask myTask = (MyTask)r;
				System.out.println("task-" + myTask.getId() + " used time is " + (currMill - myTask.getStartExecTime()) + "ms");
			}

			/**
			 * 线程池退出时触发
			 */
			@Override
			protected void terminated() {
				System.out.println("=========== thread pool exit ==============");
			}
		};
		
		for (int i = 0; i < 5; i++) {
			MyTask task = new MyTask(i);
			es.execute(task);
		}
		es.shutdown();
	}
	
}
