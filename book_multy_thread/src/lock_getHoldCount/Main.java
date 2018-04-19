package lock_getHoldCount;

import java.util.concurrent.TimeUnit;

public class Main {
	public static void main(String[] args) throws Exception {
		Service service = new Service();
		
		for (int i = 0; i < 10; i++) {
			MyThread myThread = new MyThread(service);
			myThread.start();
		}
		TimeUnit.SECONDS.sleep(2);
		System.out.println(service.lock.getHoldCount());//这个方法指的是当前线程调用lock的次数,而不是所有线程掉lock方法的次数
	}
}
