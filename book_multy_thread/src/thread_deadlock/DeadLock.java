package thread_deadlock;

public class DeadLock implements Runnable{

	private String name;

	public void setName(String name) {
		this.name = name;
	}

	private Object lock1 = new Object();
	
	private Object lock2 = new Object();
	
	@Override
	public void run() {
		if (name.equals("a")) {
			synchronized (lock1) {
				try {
					System.out.println("lock1->lock2 lock1.name = " + name);
					Thread.sleep(3000);
				} catch (Exception e) {
					e.printStackTrace();
				}
				synchronized (lock2) {
					System.out.println("lock1->lock2 lock2.name = " + name);
				}
			}
		}
		
		if (name.equals("b")) {
			synchronized (lock2) {
				try {
					System.out.println("lock2->lock1 lock2.name = " + name);
					Thread.sleep(3000);
				} catch (Exception e) {
					e.printStackTrace();
				}
				synchronized (lock1) {
					System.out.println("lock2->lock1 lock1.name = " + name);
				}
			}
		}
		
	}
	
}
