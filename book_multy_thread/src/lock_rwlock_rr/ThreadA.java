package lock_rwlock_rr;

public class ThreadA extends Thread {

	private Service service;
	
	public ThreadA (Service service) {
		this.service = service;
	}
	
	@Override
	public void run() {
		service.read();
	}

}
