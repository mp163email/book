package lock_optim_notice;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程局部变量
 * ThreadLocal可以手动remove清除存在线程threadLocalMap里的值
 * ThreadLocal,在set方法里把自己作为key(弱引用),把设置的对象作为value,放到访问他的线程Thread对象里,这个方法好有意思,在自己的方法里,把自己给卖出去了*^_^*
 * 所以当把ThreadLocal对象设置为null后,在调用System.gc(),强引用失去,弱引用就会被垃圾回收
 * @author mp
 * @date 2016-7-14 下午4:29:42
 */
public class ThreadLocal_GC {
	
	/**
	 * 重载垃圾回收
	 */
	private static volatile ThreadLocal<SimpleDateFormat> tlsdf = new ThreadLocal<SimpleDateFormat>(){

		@Override
		protected void finalize() throws Throwable {
			System.out.println("=====threadlocal is gc");
		}
		
	};
	
	/**
	 * **这种方式来判断线程池中的所有线程都执行完毕**
	 */
	private static volatile CountDownLatch cd = new CountDownLatch(10);
	
	
	/**
	 * 多线程操作时间转换
	 * @author mp
	 * @date 2016-7-14 下午4:32:29
	 */
	public static class ParseDate implements Runnable {

		@SuppressWarnings("serial")
		@Override
		public void run() {
			try {
				//先判断此线程上,有没有这个时间转换对象,如果没有就分配一个新的(注意分配的是新的,而不是把一个相同的对象分配给各个线程)
				if (tlsdf.get() == null) {
					tlsdf.set(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"){
						@Override
						protected void finalize() throws Throwable {
							System.out.println("-----simpleDateFormate is gc");
						}
					});
				}
				SimpleDateFormat sdf = tlsdf.get();
				sdf.parse("2015-03-29 21:21:21");
			} catch (ParseException e) {
				e.printStackTrace();
			} finally {
				cd.countDown();
			}
		}
	}
	
	/**
	 * 主函数
	 * @author mp
	 * @date 2016-7-14 下午4:34:11
	 * @param args
	 * @throws InterruptedException 
	 * @Description
	 */
	public static void main(String[] args) throws InterruptedException {
		ExecutorService exec = Executors.newFixedThreadPool(10);
		for (int i = 0; i < 10; i++) {
			exec.execute(new ParseDate());
		}
		cd.await();
		tlsdf = null;
		System.gc();//为什么这时候simpledateformat没有被回收呢?非得等到下一次在重新set的时候才被回收,不是显示的gc了么
		System.out.println("first gc complete");
		Thread.sleep(1 * 1000);
		
		tlsdf = new ThreadLocal<SimpleDateFormat>();
		cd = new CountDownLatch(10);
		for (int i = 0; i < 10; i++) {
			exec.execute(new ParseDate());
		}
		cd.await();
		System.gc();//为什么simpledateformat是部分被回收,而不是全部,而且被回收的个数每次执行也不一样
		System.out.println("second gc complete");
		Thread.sleep(3 * 1000);
		
		exec.shutdown();
	}
	
}
