package jdk_concurrent_package.countdownlatch;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 倒计数-门阀  用到两个方法countDown, await
 * 类似火箭点火，其他检查线程都执行完毕后,点火线程才启动.有10项需要检查,负责检查的可能正好是10个人,也可能是5个人,每个人检查两项,但这都不影响只有10项都检查完毕后才点火
 * @author mp
 * @date 2016-7-12 下午2:08:40
 */
public class CoutDownLatchDemo {
	
	public static CountDownLatch latch = new CountDownLatch(10);//有10个检查线程
	
	/**
	 * 负责检查的线程(假设10项检查都是用这一个线程,实际情况中肯定不是)
	 * @author mp
	 * @date 2016-7-12 下午2:16:07
	 */
	public static class Check extends Thread {

		@Override
		public void run() {
			int checkUsedTime = new Random().nextInt(10 * 1000);//10秒以下
			try {
				Thread.sleep(checkUsedTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println(Thread.currentThread().getName() + " check complete");
			latch.countDown();//检查完,倒计时减一, 当减少到0的时候,线程仍然可以执行,但会立即notify给await的线程
		}
	}
	
	/**
	 * 检查线程执行者线程
	 * @author mp
	 * @date 2016-7-12 下午2:18:06
	 */
	public static class Exector extends Thread {

		@Override
		public void run() {
			Check check = new Check();//多线程执行同一套检查流程
			ExecutorService exec = Executors.newFixedThreadPool(3);//初始化一个含3个线程的线程池,之所以不是10,是为了说明并不是各自的线程coutDown才有效,而他的含义是被线程调用的次数,不分是哪个线程调的
			for (int i = 0; i < 12; i++) {//这里写成12,是为了测试倒计数器减到0的时候,还会不会执行,不然写10就行
				exec.execute(check);
			}
			exec.shutdown();
		}
	}
	
	/**
	 * 点火线程
	 * @author mp
	 * @date 2016-7-12 下午2:32:16
	 */
	public static class Fire extends Thread {

		@Override
		public void run() {
			try {
				latch.await();//当前线程await, 当CountDownLatch倒计数到0的时候,就会被其通知
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Fire!");
		}
	}
	
	/**
	 * 主函数
	 * @author mp
	 * @date 2016-7-12 下午2:22:55
	 * @param args
	 * @throws InterruptedException 
	 * @Description
	 */
	public static void main(String[] args) throws InterruptedException {
		new Thread(new Exector()).start();//以一个单独的线程执行检查操作
		new Thread(new Fire()).start();//启动点火线程, 但点火线程会等待, 待检查线程都执行完,点火线程会真正执行
	}
	
}
