package pairing_mode_calculation.parall_sort;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 并行-奇偶排序
 * @author mp
 * @date 2016-7-25 下午5:51:20
 */
public class Parall_ParitySort {
	
	private static int [] arrays = {5, 2, 3, 10, 1, 100};
	
	private static int exchange = 1;
	
	/**
	 * 因为多线程操作修改这个值,所以要加同步
	 * @author mp
	 * @date 2016-7-25 下午5:58:31
	 * @param v
	 * @Description
	 */
	synchronized private static void setExchange (int v) {
		exchange = v;
	}
	
	/**
	 * 因为多线程操作读取这个值,所以要加同步
	 * @author mp
	 * @date 2016-7-25 下午5:58:31
	 * @param v
	 * @Description
	 */
	synchronized private static int getExchange () {
		return exchange;
	}
	
	/**
	 * 由于整个比较交换独立分割为奇阶段和偶阶段,这就使得在每一个阶段内所有的比较和交换是没有数据相关性的，因此每一次比较和交换都可以独立执行,也就可以并行化了
	 * 比较交换,因为其下标是互不相关的,所以可以由多线程并行来做
	 * @author mp
	 * @date 2016-7-25 下午5:56:07
	 */
	public static class OddEvenSortTask implements Runnable {
		
		private int i;
		
		private CountDownLatch latch;
		
		public OddEvenSortTask (int i, CountDownLatch latch) {
			this.i = i;
			this.latch = latch;
		}
		
		@Override
		public void run() {
			if (arrays[i] > arrays[i + 1]) {
				int tmp = arrays[i];
				arrays[i] = arrays[i + 1];
				arrays[i + 1] = tmp;
				setExchange(1);
			}
			latch.countDown();
		}
	}
	
	/**
	 * 主函数
	 * @author mp
	 * @date 2016-7-25 下午5:58:25
	 * @param args
	 * @throws InterruptedException 
	 * @Description
	 */
	public static void main(String[] args) throws InterruptedException {
		print();
		int start = 0;
		ExecutorService executor = Executors.newCachedThreadPool();
		while (getExchange() == 1 || start == 1) {
			setExchange(0);
			CountDownLatch latch = new CountDownLatch(arrays.length / 2 - (arrays.length % 2 == 0 ? start : 0));//根据数组长度和是奇交换还是偶交换,算出交换的次数(也就是要参与的线程数)
			for (int i = start; i < arrays.length - 1; i += 2) {
				executor.submit(new OddEvenSortTask(i, latch));
			}
			latch.await();
			if (start == 0) {
				start = 1;
			} else {
				start = 0;
			}
		}
		executor.shutdown();
		print();
	}
	
	/**
	 * 打印数组
	 * @author mp
	 * @date 2016-7-25 下午3:45:17
	 * @Description
	 */
	public static void print() {
		System.out.println(Arrays.toString(arrays));
	}
	
}
