package lock_product_consume;

public class Consumer extends Thread {
	
	Service service = null;
	
	public Consumer (Service service) {
		this.service = service;
	}

	@Override
	public void run() {
		while (true) {
			service.get();
		}
	}
	
}
