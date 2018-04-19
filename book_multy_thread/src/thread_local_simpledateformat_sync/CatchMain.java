package thread_local_simpledateformat_sync;

import java.text.SimpleDateFormat;

public class CatchMain {
	public static void main(String[] args) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String [] dateArray = new String[] {"2016-05-01","2016-05-02","2016-05-03","2016-05-04","2016-05-05","2016-05-06","2016-05-07","2016-05-08","2016-05-09","2016-05-10"};
		for (int i = 0; i < dateArray.length; i++) {
			CatchMyThread myThread = new CatchMyThread(sdf, dateArray[i]);
			myThread.start();
//			Thread.sleep(1);
		}
	}
}