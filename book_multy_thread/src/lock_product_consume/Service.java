package lock_product_consume;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Service {
	
	private Lock lock = new ReentrantLock();
	
	private Condition condition = lock.newCondition();
	
	private boolean flag = false;
	
	public void set () {
		try {
			lock.lock();
			while (flag == true) {
				System.out.println("COMMON SET");
				condition.await();
			}
			System.out.println("SET");
			flag = true;
			condition.signalAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
	
	public void get () {
		try {
			lock.lock();
			while (flag == false) {
				System.out.println("COMMON GET");
				condition.await();
			}
			System.out.println("GET");
			flag = false;
			condition.signalAll();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
}
