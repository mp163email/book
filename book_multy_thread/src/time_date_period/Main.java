package time_date_period;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

public class Main {
	public static void main(String[] args) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.SECOND, 10);
		
		MyTask myTask = new MyTask();
		
		Timer timer = new Timer();//构造函数可传boolean,true=守护线程
		System.out.println("--now--" + new Date());
		timer.schedule(myTask, calendar.getTime(), 1000);
	}
}
