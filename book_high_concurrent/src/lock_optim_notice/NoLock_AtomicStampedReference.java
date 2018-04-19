package lock_optim_notice;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * 带时间戳的对象引用,时间戳没被访问一次就加1
 * 有了时间戳确保了,在某一个时间戳上只能发生一次,因为当前时间戳是不变的,而内部的时间戳是时刻发生变化的
 * @author mp
 * @date 2016-7-15 下午4:45:57
 */
public class NoLock_AtomicStampedReference {
	
	private static AtomicStampedReference<Integer> money = new AtomicStampedReference<Integer>(19, 0);
	
	public static void main(String[] args) {

		//3个充值线程,不停的充值,但因为其原子性能保证只有一次成功
		for (int i = 0; i < 3; i++) {
			final int timestamp = money.getStamp();//开了三个线程,没个线程开启的时候,都拿到一个自己的时间戳,并且随着while(true)并不发生变化,而引用内部的时间戳是不断发生变化的
			new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						while (true) {
							Integer m = money.getReference();
							if (m < 20) {
								if (money.compareAndSet(m, m + 20, timestamp, timestamp + 1)) {
									System.out.println("余额小于20元,充值成功,最新余额为：" + money.getReference() + "元");
								} 
							} else {
//								System.out.println("余额大于20元,无需充值");
								break;
							}
						}
					}
				}
			}).start();
		}
		
		//消费线程
		new Thread(){
			@Override
			public void run() {
				
				for (int i = 0; i < 100; i++) {
					while (true) {
						int timestamp = money.getStamp();
						Integer m = money.getReference();
						if (m > 10) {
							System.out.println("大于10元");
							if (money.compareAndSet(m, m - 10, timestamp, timestamp + 1)) {
								System.out.println("成功消费10元,余额：" + money.getReference());
								break;
							}
						} else {
							System.out.println("没有足够的金额");
							break;
						}
					}
					try {
						TimeUnit.MILLISECONDS.sleep(100);
					} catch (Exception e) {
					}
				}
			}
		}.start();
		
	}
}
