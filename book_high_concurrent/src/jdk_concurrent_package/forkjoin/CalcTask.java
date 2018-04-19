package jdk_concurrent_package.forkjoin;

import java.util.concurrent.RecursiveTask;

/**
 * 实际计算-将fork/join 跟实际计算逻辑剥离
 * @author mp
 * @date 2016-7-13 下午5:43:03
 */
@SuppressWarnings("serial")
public class CalcTask extends RecursiveTask<Long> {
	
	private long start;
	
	private long end;
	
	public CalcTask (long start, long end) {
		this.start = start;
		this.end = end;
	}

	@Override
	protected Long compute() {
		long sum = 0;
		for (long i = start; i <= end; i++) {
			sum += i;
		}
		System.out.println(Thread.currentThread().getName() + " calc sum = " + sum);
		return sum;
	}
	
}
