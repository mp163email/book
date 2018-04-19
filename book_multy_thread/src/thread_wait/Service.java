package thread_wait;

public class Service {
	public void test_wait (Object lock) {
		try {
			synchronized (lock) {
				System.out.println(Thread.currentThread().getName() + " wait before");
				lock.wait();
				System.out.println(Thread.currentThread().getName() + " wait after");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
