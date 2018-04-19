package pairing_mode_calculation.product_consum_disruptor;

import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import com.lmax.disruptor.RingBuffer;

/**
 * 生产者
 * @author mp
 * @date 2016-7-21 下午2:32:34
 */
public class Producer implements Runnable {

	private final RingBuffer<PCData> ringBuffer;
	
	private Random random = new Random();
	
	private static AtomicInteger ai = new AtomicInteger();
	
	public Producer (RingBuffer<PCData> ringBuffer) {
		this.ringBuffer = ringBuffer;
	}
	
	private volatile boolean isRunning = true;
	
	/**
	 * 向环形队列放数据
	 * @author mp
	 * @date 2016-7-21 下午2:37:18
	 * @Description
	 */
	public void pushData (ByteBuffer bb) {
		long sequence = ringBuffer.next();//获取序列号

		//为啥要加try-finally
		try {
			PCData event = ringBuffer.get(sequence);
			event.setValue(bb.getLong(0));
		} finally {
			ringBuffer.publish(sequence);
		}
	}
	
	/**
	 * 停止生产
	 * @author mp
	 * @date 2016-7-21 下午2:45:03
	 * @Description
	 */
	public void stop () {
		isRunning = false;
	}
	
	@Override
	public void run() {
		try {
			while (isRunning) {
				Thread.sleep(random.nextInt(1000));
				ByteBuffer bb = ByteBuffer.allocate(8);
				bb.putLong(0, ai.incrementAndGet());
				pushData(bb);
				System.out.println(Thread.currentThread().getId() + " add data " + ai.get());
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
