package thread_wait;

public class ThreadC extends Thread {

	private Object lock;
	
	public ThreadC (Object lock) {
		this.lock = lock;
	}
	
	@Override
	public void run() {
		synchronized (lock) {
			lock.notifyAll();
		}
	}
	
}
