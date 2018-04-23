package thread_catch_handler;

import java.lang.Thread.UncaughtExceptionHandler;

public class CaughtExceptionHandler implements UncaughtExceptionHandler {

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		System.out.println(t.getName() + " 出现异常");
		e.printStackTrace();
	}
	
}
