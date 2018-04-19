package lock_condition;

import java.util.concurrent.TimeUnit;

public class CondMain {
	public static void main(String[] args) throws Exception{
		Service service = new Service();
		
		ThreadA threadA = new ThreadA(service);
		threadA.start();
		
		TimeUnit.SECONDS.sleep(5);
		
		service.signal_method();
	}
}
