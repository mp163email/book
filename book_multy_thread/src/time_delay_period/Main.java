package time_delay_period;

import java.util.Date;
import java.util.Timer;

public class Main {
	public static void main(String[] args) {
		MyTask myTask = new MyTask();

		Timer timer = new Timer();//构造函数可传boolean,true=守护线程
		System.out.println("--now--" + new Date());
		timer.schedule(myTask, 10 * 1000, 1000);
	}
}
