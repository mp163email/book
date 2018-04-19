package jdk_concurrent_package.forkjoin;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * 数列求和-将fork/join 跟实际计算逻辑剥离
 * @author mp
 * @date 2016-7-13 下午4:35:49
 */
@SuppressWarnings("serial")
public class CountTask extends RecursiveTask<Long> {

	private static final int tt = 20001;//手动计算的,其实可以用间隔+1即可，只是触发一个被分解后,可以进行计算的条件而已
	
	private long start;
	
	private long end;
	
	public CountTask (long start, long end) {
		this.start = start;
		this.end = end;
	}
	
	/**
	 * 计算方法（抽象方法）-必须要实现的,在这里边实现fork-join操作
	 */
	@Override
	protected Long compute() {
		long sum = 0;
		boolean canCompute = (end - start) < tt;
		if (canCompute) {
			System.out.println(Thread.currentThread().getName() + "if start = " + start + " end = " + end);
			for (long i = start; i <= end; i ++) {
				sum += i;
			}
		} else {
			System.out.println(Thread.currentThread().getName() + " else start = " + start + " end = " + end);
			long step = (start + end) / 10;//拆分成100份
			ArrayList<CountTask> subTasks = new ArrayList<>();//存储被fork的对象,以便最后统一求结果
			long pos = start;//初始第一次的第一个数
			
			//分100个线程
			for (int i = 0; i < 10; i++) {
				long lastOne = pos + step;//计算每一份的最后一个数
				if (lastOne > end) {//处理有可能带余数的情况,非整除的情况
					lastOne = end;
				}
				CountTask subTask = new CountTask(pos, lastOne);
				pos = pos + step + 1;//计算每一份的第一个数
				subTasks.add(subTask);
				subTask.fork();//将此任务对象分派给池中的一个线程
			}
			for (CountTask t : subTasks) {
				sum += t.join();//单个线程的计算结果可以通过join来获取，join方法是阻塞的（也就是说else里边的线程会一直等到所有分派的线程计算完,生命周期才会结束）
			}
			System.out.println(Thread.currentThread().getName() + " else sum = " + sum);//else里边的线程从一开始到计算完,始终使用的是一个线程而不变
		}
		return sum;
	}
	
	/**
	 * 主函数
	 * @author mp
	 * @date 2016-7-13 下午5:05:55
	 * @param args
	 * @Description
	 */
	public static void main(String[] args) {
		ForkJoinPool forkJoinPool = new ForkJoinPool();
		CountTask task = new CountTask(0, 20 * 10000L + 1);//20 * 10000
		ForkJoinTask<Long> result = forkJoinPool.submit(task);
		try {
			long rs = result.get();//整个任务的计算结果通过get来获取
			System.out.println(rs);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		
	}
}
