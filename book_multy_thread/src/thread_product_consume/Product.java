package thread_product_consume;

/**
 * 生产者
 * @author mp
 * @date 2016-4-26 下午1:58:09
 */
public class Product extends Thread {

	Container container = null;
	
	public Product (Container container) {
		this.container = container;
	}
	
	@Override
	public void run() {
		while (true) {
			container.put();
		}
	}
	
}
