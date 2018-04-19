package lock_getHoldCount;

public class MyThread extends Thread {
	
	Service service = new Service();
	
	public MyThread (Service service) {
		this.service = service;
	}

	@Override
	public void run() {
		service.method();
	}
	
}
