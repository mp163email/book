package lock_rwlock_rr;

public class Main {
	public static void main(String[] args) throws Exception{
		Service service = new Service();
		ThreadA threadA = new ThreadA(service);
		threadA.start();
		
		Thread.sleep(3000);
		
		ThreadA threadA2 = new ThreadA(service);
		threadA2.start();
	}
}
