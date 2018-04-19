package jdk_concurrent_package.threadpool_catch;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 自定义能抛出异常的线程池-不但能看到报错的地方还能看到在哪调用的
 * @author mp
 * @date 2016-7-13 下午3:05:56
 */
public class TraceThreadPool extends ThreadPoolExecutor {

	/**
	 * 默认构造方法
	 * @param corePoolSize
	 * @param maximumPoolSize
	 * @param keepAliveTime
	 * @param unit
	 * @param workQueue
	 */
	public TraceThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	}

	/**
	 * 重写执行方法-加上异常
	 */
	@Override
	public void execute(Runnable command) {
		super.execute(catchRunnable(command, clientException (), Thread.currentThread().getName()));
	}

	/**
	 * 重写执行方法-加上异常
	 */
	@Override
	public Future<?> submit(Runnable task) {
		return super.submit(catchRunnable(task, clientException (), Thread.currentThread().getName()));
	}
	
	/**
	 * 包装一个自定义异常
	 * @author mp
	 * @date 2016-7-13 下午3:14:16
	 * @return
	 * @Description
	 */
	private Exception clientException () {
		return new Exception("Client stack trance ");
	}
	
	/**
	 * 包装一个带异常的Runnable
	 * @author mp
	 * @date 2016-7-13 下午3:16:24
	 * @param task
	 * @param exception
	 * @param clientThreadName//这个参数有用么?难道说没有这个参数,在异常堆栈中就打印不出线程名字么??
	 * @return
	 * @Description
	 */
	private Runnable catchRunnable (final Runnable task, final Exception exception, String clientThreadName) {
		
		return new Runnable() {
			@Override
			public void run() {
				try {
					task.run();
				} catch (Exception e) {
					exception.printStackTrace();//将堆栈保存到这个自定义异常里边(但两个不是同一个异常呀,这样会保存么???)
					throw e;//仍然往外抛出异常
				}
			}
		};
	}
}
