package lock_many_condition;

import java.util.concurrent.TimeUnit;

public class ManyConMain {
	public static void main(String[] args) throws Exception{
		Service service = new Service();
		
		ThreadA threadA = new ThreadA(service);
		threadA.start();
		
		ThreadB threadB = new ThreadB(service);
		threadB.start();
		
		TimeUnit.SECONDS.sleep(5);
		
		service.signalA_method();
		
		TimeUnit.SECONDS.sleep(2);
		
		service.signalB_method();
	}
}
