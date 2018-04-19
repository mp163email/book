package thread_local_simpledateformat_sync;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CatchMyThread extends Thread {

	SimpleDateFormat sdf = null;
	String dateStr = "";
	
	public CatchMyThread (SimpleDateFormat sdf, String dateStr) {
		this.sdf = sdf;
		this.dateStr = dateStr;
	}
	
	@Override
	public void run() {
		try {
			Date parseDate = sdf.parse(dateStr);
			String formatStr = sdf.format(parseDate);
			if (!dateStr.equals(formatStr)) {
				System.out.println("并发造成数据错误");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
