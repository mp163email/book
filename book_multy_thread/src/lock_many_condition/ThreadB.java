package lock_many_condition;

public class ThreadB extends Thread {

	private Service service;
	
	public ThreadB (Service service) {
		this.service = service;
	}
	
	@Override
	public void run() {
		service.waitB_method();
	}
}
