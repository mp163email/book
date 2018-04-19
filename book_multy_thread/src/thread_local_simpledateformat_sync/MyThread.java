package thread_local_simpledateformat_sync;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyThread extends Thread {

	SimpleDateFormat sdf = null;
	String dateStr = "";
	
	public MyThread (SimpleDateFormat sdf, String dateStr) {
		this.sdf = sdf;
		this.dateStr = dateStr;
	}
	
	@Override
	public void run() {
		try {
			SimpleDateFormat simpleDateFormat = SimpleDateTools.getSimpleDateFormat();
//			System.out.println(Thread.currentThread().getName() + " " + simpleDateFormat.hashCode());//这里获取的hashcode竟然相同,也就是说是一个对象（但在不同的线程,存的是不同的值,怎么做到的？）
			Date parseDate = simpleDateFormat.parse(dateStr);
			String formatStr = simpleDateFormat.format(parseDate);
			if (!dateStr.equals(formatStr)) {
				System.out.println("并发造成数据错误");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
