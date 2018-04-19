package pairing_base.bad_lock;

/**
 * Integer的错误加锁方式
 * 结果：值会比预期的要小
 * 原因：两个Integer对象不是不变的,Integer属于不变对象,每一个新值,都会创建一个新的对象
 * @author mp
 * @date 2016-7-11 下午3:01:37
 */
public class BadLockOnInteger implements Runnable {
	
	public static Integer num = 0;
	
	static BadLockOnInteger instance = new BadLockOnInteger();

	@Override
	public void run() {
		for (int i = 0; i < 1000 * 10000; i++) {
			synchronized (num) {
				num++;
			}
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		Thread t1 = new Thread(instance);
		Thread t2 = new Thread(instance);
		t1.start(); t2.start();
		t1.join(); t2.join();
		System.out.println(num);
	}
}
