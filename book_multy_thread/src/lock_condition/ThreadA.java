package lock_condition;

public class ThreadA extends Thread{

	private Service service;
	
	public ThreadA (Service service) {
		this.service = service;
	}
	
	@Override
	public void run() {
		service.wait_method();
	}
}
