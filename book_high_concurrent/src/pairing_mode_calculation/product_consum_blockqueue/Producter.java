package pairing_mode_calculation.product_consum_blockqueue;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 生产者
 * @author mp
 * @date 2016-7-20 下午5:29:13
 */
public class Producter implements Runnable {
	
	private volatile boolean isRunning = true;//用来决定生产者是否继续生产

	private static AtomicInteger count = new AtomicInteger();//原子操作
	
	private static final int sleeptime = 1000;//休眠上限-毫秒
	
	private BlockingQueue<PCData> queue;//用于生产者和消费者之间的数据缓冲区,这样就可以允许生产者线程和消费者线程存在执行上的性能差异
	
	public Producter (BlockingQueue<PCData> queue) {
		this.queue = queue;
	}

	@Override
	public void run() {
		PCData data = null;
		Random r = new Random();
		System.out.println("start producter id = " + Thread.currentThread().getId());
		try {
			while (isRunning) {
				Thread.sleep(r.nextInt(sleeptime));
				data = new PCData(count.incrementAndGet());
				System.out.println(data + " is put into queue");
				if (!queue.offer(data, 2, TimeUnit.SECONDS)) {
					System.out.println("fail to put data : " + data);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
	}
	
	/**
	 * 停止生产
	 * @author mp
	 * @date 2016-7-20 下午5:48:40
	 * @Description
	 */
	public void stop () {
		isRunning = false;
	}
	
}
