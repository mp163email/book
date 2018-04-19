package lock_product_consume;

public class Main {
	
	public static void main(String[] args) {
		Service service = new Service();
		for (int i = 0; i < 5; i++) {
			Product product = new Product(service);
			product.start();
		}
		
		for (int i = 0; i < 2; i++) {
			Consumer consumer = new Consumer(service);
			consumer.start();
		}
	}
	
}
