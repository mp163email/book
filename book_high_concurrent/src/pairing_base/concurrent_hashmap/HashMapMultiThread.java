package pairing_base.concurrent_hashmap;

import java.util.HashMap;

/**
 * 测试多线程下HashMap 不安全
 * 结果：死循环,CPU飙高,很恐怖,多线程环境下一定不要用HashMap(Jdk8已不会死循环,但会丢数据）
 * 原因：put的时候,HashMap会循环迭代内部数据,由于非线程安全,内部链表遭到破坏,形成闭环,也就是死循环
 * @author mp
 * @date 2016-7-11 下午2:24:36
 */
public class HashMapMultiThread {

	static HashMap<String, String> map = new HashMap<>();
	
	public static class AddThread implements Runnable {

		int start = 0;//定义一个初始值
		
		public AddThread (int start) {
			this.start = start;
		}
		
		@Override
		public void run() {
			for (int i = start; i < 10 * 10000; i+=2) {//然后配合这种方法很好
//				synchronized (HashMapMultiThread.class) {
					map.put(Integer.toString(i), Integer.toBinaryString(i));
//				}
			}
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		Thread t1 = new Thread(new AddThread(0));
		Thread t2 = new Thread(new AddThread(1));
		t1.start(); t2.start();
		t1.join(); t2.join();
		System.out.println(map.size());
	}
	
}
