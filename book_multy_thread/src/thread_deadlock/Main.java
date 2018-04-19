package thread_deadlock;

import java.util.concurrent.TimeUnit;

public class Main {
	public static void main(String[] args) throws Exception{
		DeadLock deadLock = new DeadLock();//同一个runnable
		Thread thread1 = new Thread(deadLock);
		deadLock.setName("a");
		thread1.start();
		
		TimeUnit.MILLISECONDS.sleep(300);
		
		Thread thread2 = new Thread(deadLock);
		deadLock.setName("b");
		thread2.start();
	}
}
