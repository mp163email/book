package pairing_mode_calculation.product_consum_blockqueue;

import java.text.MessageFormat;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

/**
 * 消费者
 * @author mp
 * @date 2016-7-20 下午5:49:24
 */
public class Consumer implements Runnable {
	
	private BlockingQueue<PCData> queue;
	
	private static final int sleeptime = 1000;
	
	public Consumer (BlockingQueue<PCData> queue) {
		this.queue = queue;
	}

	@Override
	public void run() {
		System.out.println("start Consumer id = " + Thread.currentThread().getId());
		Random r = new Random();
		try {
			while (true) {
				PCData data = queue.take();
				if (null != data) {//不阻塞,空的话返回null
					int re = data.getData() * data.getData();//计算平方
					System.out.println(MessageFormat.format("{0} * {1} = {2}", data.getData(), data.getData(), re));
					Thread.sleep(r.nextInt(sleeptime));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Thread.currentThread().interrupt();
		}
	}
	
}
