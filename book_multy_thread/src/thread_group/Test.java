package thread_group;

import java.util.concurrent.TimeUnit;

public class Test {
	public static void main(String[] args) throws Exception {

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						System.out.println(Thread.currentThread().getName() + "  aaaaaaaaaaaaaaaa");
						TimeUnit.SECONDS.sleep(1);
					} catch (Exception e) {
					}
				}
			}
		}).start();
		
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						System.out.println(Thread.currentThread().getName() + "  bbbbbbbbbb");
						TimeUnit.SECONDS.sleep(1);
					} catch (Exception e) {
					}
				}
			}
		}).start();
		
		TimeUnit.SECONDS.sleep(5);
		
		System.out.println("main --------" + Thread.currentThread().getThreadGroup().activeCount());
		
		Thread[] threadArray = new Thread[Thread.currentThread().getThreadGroup().activeCount()];//线程组中的活动线程
		Thread.currentThread().getThreadGroup().enumerate(threadArray);
		for (int i = 0; i < threadArray.length; i++) {
			Thread thread = threadArray[i];
			System.out.println(thread.getName() + "     " + thread.getState());
		}
		
	}
}
