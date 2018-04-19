package thread_turn;

public class Service {
	
	volatile private boolean flag = false;//变量多线程可见，各个线程取数据时，用的是公共变量，而非线程自己的变量
	
	synchronized public void methodA () {
		try {
			while (flag == true) {
				wait();
			}
			System.out.println(Thread.currentThread().getId());
			System.out.println("XXXXXXXXXXX");
			flag = true;
			notifyAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	synchronized public void methodB () {
		try {
			while (flag == false) {
				wait();
			}
			System.out.println("OOOOOOOOOOO");
			flag = false;
			notifyAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
