package thread_local_simpledateformat_sync;

import java.text.SimpleDateFormat;

public class SimpleDateTools {
	
	private static ThreadLocal<SimpleDateFormat> threadLocal = new ThreadLocal<SimpleDateFormat>();//这里虽然是静态的,但是,在不同的线程里执行的时候,都有各自自己的值（隔离性）
	
	public static SimpleDateFormat getSimpleDateFormat () {
		SimpleDateFormat sdf = null;
		sdf = threadLocal.get();
		if (sdf == null) {
			sdf = new SimpleDateFormat("yyyy-MM-dd");
			threadLocal.set(sdf);
		}
		return sdf;
	}
}
