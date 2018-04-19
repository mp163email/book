package pairing_base.concurrent_arraylist;

import java.util.ArrayList;

/**
 * 测试多线程下的ArrayList,不安全
 * @author mp
 * @date 2016-7-11 下午1:59:30
 */
public class ArrayListMultiThread {
	
	static ArrayList<Integer> al = new ArrayList<>(10);//初始数组大小容量为10,每次扩容为原来的1.5倍
	
	/**
	 * 静态内部类
	 * @author mp
	 * @date 2016-7-11 下午2:14:51
	 */
	public static class AddThread implements Runnable {

		@Override
		public void run() {
			for (int i = 0; i < 100 * 10000; i++) {
//				synchronized (ArrayListMultiThread.class) {
					al.add(i);
//				}
			}
		}
	}

	public static void main(String[] args) throws InterruptedException {
		Thread t1 = new Thread(new AddThread());
		Thread t2 = new Thread(new AddThread());
		t1.start();
		t2.start();
		
		t1.join(); t2.join();
		System.out.println(al.size());
	}
	
}
