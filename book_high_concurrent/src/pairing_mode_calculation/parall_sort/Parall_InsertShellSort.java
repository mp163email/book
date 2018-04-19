package pairing_mode_calculation.parall_sort;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 并行-希尔排序（插入排序的变种）
 * @author mp
 * @date 2016-7-26 下午2:34:05
 */
public class Parall_InsertShellSort {
	
	private static int [] arrays = {10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
	
	/**
	 * 比较排序
	 * @author mp
	 * @date 2016-7-26 下午2:38:48
	 */
	public static class ShellSortTask implements Runnable {
		int i = 0;
		int h = 0;
		CountDownLatch l;
		
		public ShellSortTask (int i, int h, CountDownLatch l) {
			this.i = i;
			this.h = h;
			this.l = l;
		}

		@Override
		public void run() {
			if (arrays[i] < arrays[i - h]) {
				int tmp = arrays[i];
				int j = i - h;
				while (j >= 0 && arrays[j] > tmp) {
					arrays[j + h] = arrays[j];
					j -= h;
				}
				arrays[j + h] = tmp;
			}
			l.countDown();
		}
		
	}
	
	
	/**
	 * 主函数
	 * @author mp
	 * @date 2016-7-26 下午2:35:23
	 * @param args
	 * @throws InterruptedException 
	 * @Description
	 */
	public static void main(String[] args) throws InterruptedException {
		print();
		ExecutorService pool = Executors.newCachedThreadPool();
		int h = 1;
		CountDownLatch latch = null;
		while (h <= arrays.length / 3) {
			h = h * 3 + 1;
		}
		while (h > 0) {
			if (h >= 4) {
				latch = new CountDownLatch(arrays.length - h);
			}
			for (int i = h; i < arrays.length; i++) {
				if (h >= 4) {//为控制线程数量,在h>=4的时候使用并行线程,否则退化为传统的插入排序
					pool.execute(new ShellSortTask(i, h, latch));
				} else {
					if (arrays[i] < arrays[i - h]) {
						int tmp = arrays[i];
						int j = i - h;
						while (j >= 0 && arrays[j] > tmp) {
							arrays[j + h] = arrays[j];
							j -= h;
						}
						arrays[j + h] = tmp;
					}
				}
			}
			latch.await();
			h = (h - 1) / 3;
		}
		pool.shutdown();
		print();
	}
	
	/**
	 * 打印数组
	 * @author mp
	 * @date 2016-7-26 下午2:35:13
	 * @Description
	 */
	public static void print() {
		System.out.println(Arrays.toString(arrays));
	}
}
