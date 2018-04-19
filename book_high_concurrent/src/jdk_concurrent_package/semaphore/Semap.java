package jdk_concurrent_package.semaphore;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * 信号量  用到两个方法acquire, release
 * 就跟停车场一样,假设停车场有3个空位,门卫能让3辆车同时进入,后来的车都得排队，不让进。停车场里边开走了1辆，门卫就在放进一辆，依次
 * 信号量管理许可，线程从信号量那拿许可，拿一个信号量的许可减一，释放一个，信号量的许可加一
 * 信号量许可数设置成1,可以实现线程间互斥
 * @author mp
 * @date 2016-7-11 下午6:00:52
 */
public class Semap implements Runnable {

	final Semaphore semaphore = new Semaphore(5);
	
	@Override
	public void run() {
		try {
			semaphore.acquire();//信号量持有的许可线程作用范围是 acquire-release之间的线程数
			Thread.sleep(2000);
			System.out.println(Thread.currentThread().getName() + ": done");
			semaphore.release();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		Semap semap = new Semap();
		ExecutorService exec = Executors.newFixedThreadPool(20);
		for (int i = 0; i < 20; i++) {
			exec.execute(semap);
		}
		exec.shutdown();
	}
}
