package thread_turn;

public class ThreadB extends Thread {

	private Service service;
	
	public ThreadB (Service service) {
		this.service = service;
	}
	
	@Override
	public void run() {
		service.methodB();
	}
	
}
