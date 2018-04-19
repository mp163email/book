package lock_product_consume;

public class Product extends Thread {
	
	Service service = null;
	
	public Product (Service service) {
		this.service = service;
	}

	@Override
	public void run() {
		while (true) {
			service.set();
		}
	}
	
}
