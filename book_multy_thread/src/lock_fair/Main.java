package lock_fair;

public class Main {
	public static void main(String[] args) {
		final Service service = new Service(true);
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				service.method();
			}
		};
		
		Thread[] threads = new Thread[10];
		for (int i = 0; i < 10; i++) {
			Thread thread = new Thread(runnable);
			threads[i] = thread;
		}
		for (int i = 0; i < 10; i++) {
			Thread thread = threads[i];
			thread.start();
		}
	}
}
