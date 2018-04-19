package lock_many_condition;

public class ThreadA extends Thread {

	private Service service;
	
	public ThreadA (Service service) {
		this.service = service;
	}
	
	@Override
	public void run() {
		service.waitA_method();
	}
}
