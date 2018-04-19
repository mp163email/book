package lock_many_condition;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Service {
	
	private Lock lock = new ReentrantLock();
	
	private Condition conditionA = lock.newCondition();
	
	private Condition conditionB = lock.newCondition();
	
	public void waitA_method () {
		try {
			lock.lock();
			System.out.println(Thread.currentThread().getName() + " wait AAA before");
			conditionA.await();
			System.out.println(Thread.currentThread().getName() + " wait AAA after");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
	
	public void signalA_method () {
		try {
			lock.lock();
			System.out.println(Thread.currentThread().getName() + " signal AAA");
			conditionA.signalAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
	
	public void waitB_method () {
		try {
			lock.lock();
			System.out.println(Thread.currentThread().getName() + " wait BBB before");
			conditionB.await();
			System.out.println(Thread.currentThread().getName() + " wait BBB after");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
	
	public void signalB_method () {
		try {
			lock.lock();
			System.out.println(Thread.currentThread().getName() + " signal BBB");
			conditionB.signalAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
}
