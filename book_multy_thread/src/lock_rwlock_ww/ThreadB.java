package lock_rwlock_ww;

public class ThreadB extends Thread {

	private Service service;
	
	public ThreadB (Service service) {
		this.service = service;
	}
	
	@Override
	public void run() {
		service.write2();
	}

}
