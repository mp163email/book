package thread_turn;

public class TurnMain {
	public static void main(String[] args) {
		Service service = new Service();
		for (int i = 0; i < 20; i++) {
			
			ThreadA threadA = new ThreadA(service);
			threadA.start();
			
			ThreadB threadB = new ThreadB(service);
			threadB.start();
		}
	}
}
