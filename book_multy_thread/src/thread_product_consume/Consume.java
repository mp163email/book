package thread_product_consume;

/**
 * 消费者
 * @author mp
 * @date 2016-4-26 下午1:59:40
 */
public class Consume extends Thread {
	
	Container container = null;
	
	public Consume (Container container) {
		this.container = container;
	}
	
	@Override
	public void run() {
		while (true) {
			container.get();
		}
	}
	
}
