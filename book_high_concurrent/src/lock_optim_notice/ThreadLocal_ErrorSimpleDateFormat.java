package lock_optim_notice;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 错误的SimpleDateFormat,因为此对象为线程不安全的
 * @author mp
 * @date 2016-7-14 下午4:29:42
 */
public class ThreadLocal_ErrorSimpleDateFormat {
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * 多线程操作时间转换
	 * @author mp
	 * @date 2016-7-14 下午4:32:29
	 */
	public static class ParseDate implements Runnable {

		@Override
		public void run() {
			try {
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
