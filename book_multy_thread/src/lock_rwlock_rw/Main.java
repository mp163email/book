package lock_rwlock_rw;

public class Main {
	public static void main(String[] args) throws Exception{
		Service service = new Service();
		ThreadA threadA = new ThreadA(service);
		threadA.start();
		
		Thread.sleep(3000);
		
		ThreadB threadb = new ThreadB(service);
		threadb.start();
	}
}
