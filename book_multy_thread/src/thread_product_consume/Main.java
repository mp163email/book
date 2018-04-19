package thread_product_consume;

public class Main {
	public static void main(String[] args) {
		Container container = new Container(100);
		for (int i = 0; i < 10; i++) {
			Product product = new Product(container);
			product.start();
		}
		
		for (int i = 0; i < 1; i++) {
			Consume consume = new Consume(container);
			consume.start();
		}
	}
}
