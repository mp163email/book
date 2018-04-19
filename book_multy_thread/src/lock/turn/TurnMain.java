package lock.turn;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class TurnMain {
	
	volatile private static int nextPrintWho = 1;
	private static ReentrantLock lock =new ReentrantLock();
	final private static Condition conditionA = lock.newCondition();
	final private static Condition conditionB = lock.newCondition();
	final private static Condition conditionC = lock.newCondition();
	
	public static void main(String[] args) {
		
		Thread threadA = new Thread(){
			@Override
			public void run() {
				try {
					lock.lock();
					while (nextPrintWho != 1) {
						conditionA.await();
					}
					for (int i = 0; i < 3; i++) {
						System.out.println("ThreadA " + (i + 1));
					}
					nextPrintWho = 2;
					conditionB.signalAll();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					lock.unlock();
				}
			}
		};
		
		Thread threadB = new Thread(){
			@Override
			public void run() {
				try {
					lock.lock();
					while (nextPrintWho != 2) {
						conditionB.await();
					}
					for (int i = 0; i < 3; i++) {
						System.out.println("ThreadB " + (i + 1));
					}
					nextPrintWho = 3;
					conditionC.signalAll();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					lock.unlock();
				}
			}
		};
		
		Thread threadC = new Thread(){
			@Override
			public void run() {
				try {
					lock.lock();
					while (nextPrintWho != 3) {
						conditionC.await();
					}
					for (int i = 0; i < 3; i++) {
						System.out.println("ThreadC " + (i + 1));
					}
					nextPrintWho = 1;
					conditionA.signalAll();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					lock.unlock();
				}
			}
		};
		
/*		Thread[] aArrays = new Thread[5];
		Thread[] bArrays = new Thread[5];
		Thread[] cArrays = new Thread[5];*/
		for (int i = 0; i < 5; i++) {
/*			aArrays[i] = new Thread(threadA);
			bArrays[i] = new Thread(threadB);
			cArrays[i] = new Thread(threadC);
			
			aArrays[i].start();
			bArrays[i].start();
			cArrays[i].start();*/
			
			new Thread(threadA).start();
			new Thread(threadB).start();
			new Thread(threadC).start();
			
		}
	}
}
