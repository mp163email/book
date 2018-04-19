package time_date_period;

import java.util.Date;
import java.util.TimerTask;

public class MyTask extends TimerTask {

	@Override
	public void run() {
		System.out.println("任务被执行 -" + new Date());
	}
	
}
