package jdk_concurrent_package.threadpool_mythreadcreate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 自定义线程创建  线程池
 * @author mp
 * @date 2016-7-13 上午11:23:28
 */
public class ThreadPoolMyCreateThreadDemo {
	
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
			System.out.println(System.currentTimeMillis() + ": Thread Name:" + Thread.currentThread().getName());
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
			new LinkedBlockingDeque<Runnable>(10), new ThreadFactory() {
				long threadId = 0;//类只创建一次
				@Override
				public Thread newThread(Runnable r) {
					Thread thread = new Thread(r);//这里要将r放到Thread的构造函数里,否则不会执行任何任务
					thread.setName("myThread-" + (threadId++));
					System.out.println(thread.getName() + " had created");
					return thread;
				}
			}
		);
		
		for (int i = 0; i < 5; i++) {
			MyTask task = new MyTask(i);
			es.execute(task);
		}
		es.shutdown();
	}
	
}
