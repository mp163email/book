package jdk_concurrent_package.cyclicbarrier;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 循环栅栏 - 只用到一个方法 await
 * 这个和CountDownLatch相比走的是加法,到指定数后清零,可以重新再来
 * @author mp
 * @date 2016-7-12 下午3:47:48
 */
public class CyclicBarrierDemo {
	
	/**
	 * 士兵
	 * @author mp
	 * @date 2016-7-12 下午4:06:47
	 */
	public static class Soldier implements Runnable {

		private String soldierName;
		
		private final CyclicBarrier cyclic;
		
		/**
		 * 构造方法初始化士兵名字,跟循环栅栏
		 * @param soldierName
		 * @param cyclic
		 */
		public Soldier (String soldierName, CyclicBarrier cyclic) {
			this.soldierName = soldierName;
			this.cyclic = cyclic;
		}
		
		/**
		 * 集合
		 * @author mp
		 * @date 2016-7-12 下午4:32:03
		 * @Description
		 */
		private void gather () {
			try {
				Thread.sleep(new Random().nextInt(5 * 1000));
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println(soldierName + ":集合OK");
		}
		
		/**
		 * 做工作
		 * @author mp
		 * @date 2016-7-12 下午4:10:37
		 * @Description
		 */
		private void doWork () {
			try {
				Thread.sleep(new Random().nextInt(5 * 1000));
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println(soldierName + ":任务OK");
		}
		
		@Override
		public void run() {
			try {
				gather();//模拟集合动作
				cyclic.await();//等待全部集合完毕：完毕后会触发一个任务给教官,计数器清零,然后程序继续往下走
				doWork();//模拟工作动作
				cyclic.await();
			} catch (InterruptedException | BrokenBarrierException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 教官：循环栅栏到指定个数后,触发的任务
	 * @author mp
	 * @date 2016-7-12 下午4:16:44
	 */
	public static class Master implements Runnable {

		private boolean flag;//用于表示是状态为集合队伍完毕还是做工作完毕
		
		public Master (boolean flag) {
			this.flag = flag;
		}
		
		@Override
		public void run() {
			if (flag) {
				System.out.println("所有士兵任务完毕");
			} else {
				System.out.println("所有士兵集合完毕");
				flag = true;//做标记,下一次不走这个逻辑了
			}
		}
	}
	
	/**
	 * 主函数
	 * @author mp
	 * @date 2016-7-12 下午4:27:59
	 * @param args
	 * @Description
	 */
	public static void main(String[] args) {
		CyclicBarrier cyclicBarrier = new CyclicBarrier(10, new Master(false));
		ExecutorService exec = Executors.newFixedThreadPool(10);
		for (int i = 0; i < 10; i++) {
			exec.execute(new Soldier("solder" + i, cyclicBarrier));
		}
		exec.shutdown();
	}
	
}
