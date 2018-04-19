package single_lazy_asyn;

public class Main {
	public static void main(String[] args) {
		for (int i = 0; i < 3; i++) {
			MyThread myThread = new MyThread();
			myThread.start();
		}
	}
}
