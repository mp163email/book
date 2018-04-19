package thread_mypool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class MyPool {
	/**
	 * 自定义一个工作线程[内部类]
	 * @author mp
	 * @date 2016-5-24 下午4:19:15
	 */
	class WorkerThread extends Thread {

		/**
		 * 自己内部维护的一个任务队列
		 */
		private BlockingQueue<Runnable> blockingqueue = new LinkedBlockingQueue<Runnable>();
		
		/**
		 * 向任务队列放任务
		 * @author mp
		 * @date 2016-5-24 下午4:28:25
		 * @param runnable
		 * @Description
		 */
		public void addTask (Runnable runnable) {
			blockingqueue.add(runnable);
		}
		
		@Override
		public void run() {
			try {
				while (true) {
					Runnable runnable = blockingqueue.take();//有任务就取出来执行,没有任务就一直阻塞
					runnable.run();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 主函数
	 * @author mp
	 * @date 2016-5-24 下午4:29:15
	 * @param args
	 * @throws Exception
	 * @Description
	 */
	public static void main(String[] args) throws Exception {
		
		/*
		 * 就绪(初始化并启动)自定义线程
		 */
		WorkerThread workerThread = new MyPool().new WorkerThread();//内部类的实例化格式, 外部类对象.new
		workerThread.start();
		
		/*
		 * 初始化外部任务队列
		 */
		ConcurrentLinkedQueue<Runnable> runnableQueue = new ConcurrentLinkedQueue<>();
		for (int i = 0; i < 10; i++) {
			final int index = i + 1;
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					System.out.println("exec " + index);
				}
			};
			runnableQueue.add(runnable);
		}
		
		/*
		 *	每隔3秒取3条来执行 
		 */
		while (true) {
			for (int i = 0; i < 3; i++) {
				Runnable runnable = runnableQueue.poll();
				if (runnable != null) {
					workerThread.addTask(runnable);
				}
			}
			TimeUnit.SECONDS.sleep(3);
			System.out.println();
		}
	}
}
