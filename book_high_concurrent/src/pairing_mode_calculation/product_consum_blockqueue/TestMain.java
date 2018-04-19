package pairing_mode_calculation.product_consum_blockqueue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 测试主函数
 * @author mp
 * @date 2016-7-20 下午5:55:14
 */
public class TestMain {
	public static void main(String[] args) throws InterruptedException {
		
		BlockingQueue<PCData> queue = new LinkedBlockingQueue<PCData>(10);//有设定值的无界队列
		
		Producter producter1 = new Producter(queue);
		Producter producter2 = new Producter(queue);
		Producter producter3 = new Producter(queue);
		
		Consumer consumer1 = new Consumer(queue);
		Consumer consumer2 = new Consumer(queue);
		Consumer consumer3 = new Consumer(queue);
		
		ExecutorService service = Executors.newCachedThreadPool();
		
		service.execute(producter1);
		service.execute(producter2);
		service.execute(producter3);
		
		service.execute(consumer1);
		service.execute(consumer2);
		service.execute(consumer3);
		
		Thread.sleep(10 * 1000);
		
		producter1.stop();
		producter2.stop();
		producter3.stop();
		
		Thread.sleep(3 * 1000);
		
		service.shutdown();
	}
}
