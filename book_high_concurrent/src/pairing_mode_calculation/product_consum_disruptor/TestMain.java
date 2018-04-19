package pairing_mode_calculation.product_consum_disruptor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WorkHandler;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

/**
 * 测试主函数
 * Disruptor-生产者,消费者无锁的实现CAS
 * @author mp
 * @date 2016-7-21 下午2:55:20
 */
public class TestMain {
	
	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws InterruptedException {
		ExecutorService executor = Executors.newCachedThreadPool();
		PCDataFactory factory = new PCDataFactory();
		int bufferSize = 1024;//Disruptor环形队列,队列总大小必须事先指定,不能动态扩展,并且要指定为2的指数幂
		Disruptor<PCData> disruptor = new Disruptor<>(factory, bufferSize, executor, ProducerType.MULTI, new BlockingWaitStrategy());//除了BlockingWaitStrategy,还有其他3种等待策略

		//封装4个消费者
		WorkHandler<PCData>[] workHandlerArray = new WorkHandler[4];
		for (int i = 0; i < workHandlerArray.length; i++) {
			workHandlerArray[i] = new Consumer();
		}
		disruptor.handleEventsWithWorkerPool(workHandlerArray);
		disruptor.start();//注意disruptor也有start方法,不要漏掉,否则消费者线程和逻辑不起作用
		
		//封装两个生产者
		ExecutorService executor1 = Executors.newCachedThreadPool();
		RingBuffer<PCData> ringBuffer = disruptor.getRingBuffer();
		int num = 2;
		Producer[] producers = new Producer[2];
		for (int i = 0; i < num; i++) {
			producers[i] = new Producer(ringBuffer);
		}
		for (int i = 0; i < producers.length; i++) {
			executor1.execute(producers[i]);
		}
		Thread.sleep(10 * 1000);
		for (int i = 0; i < producers.length; i++) {
			producers[i].stop();
		}
	}
	
}
