package lock_optim_notice;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 人手一支笔ThreadLocal
 * 为每一个线程都产生一个SimpleDateFormat对象,然后线程和线程之间,各自用各自的,互不干扰
 * 实现原理：Thread内部有一个ThreadLocalMap,key是ThreadLocal对象(弱引用),value是设置的对象,所以根据当前线程就能拿到这个线程的value值
 * @author mp
 * @date 2016-7-14 下午4:29:42
 */
public class ThreadLocal_SimpleDateFormat {
	
	private static ThreadLocal<SimpleDateFormat> tlsdf = new ThreadLocal<SimpleDateFormat>();
	
	/**
	 * 多线程操作时间转换
	 * @author mp
	 * @date 2016-7-14 下午4:32:29
	 */
	public static class ParseDate implements Runnable {

		@Override
		public void run() {
			try {
				//先判断此线程上,有没有这个时间转换对象,如果没有就分配一个新的(注意分配的是新的,而不是把一个相同的对象分配给各个线程)
				if (tlsdf.get() == null) {
					tlsdf.set(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
				}
				SimpleDateFormat sdf = tlsdf.get();
				Date date = sdf.parse("2015-03-29 21:21:21");
				System.out.println(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 主函数
	 * @author mp
	 * @date 2016-7-14 下午4:34:11
	 * @param args
	 * @Description
	 */
	public static void main(String[] args) {
		ExecutorService exec = Executors.newFixedThreadPool(10);
		for (int i = 0; i < 100; i++) {
			exec.execute(new ParseDate());
		}
		exec.shutdown();
	}
	
}
