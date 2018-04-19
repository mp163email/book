package lock_optim_notice;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 无锁的对象引用,操作对象的,改变对象的时候,能保证对象原子性,????任何对象????
 * 本例展示,由于CAS只对比值,不记录状态,会造成修改多次后值没变,但因为修改多次造成损害的生活场景
 * 感觉这个例子不对呢,小于20就是会充钱的呀
 * @author mp
 * @date 2016-7-15 下午4:45:57
 */
public class NoLock_AtomicReference_Qustion {
	
	private static AtomicReference<Integer> money = new AtomicReference<Integer>(19);
	
	public static void main(String[] args) {
		
		//3个充值线程,不停的充值,但因为其原子性能保证只有一次成功
		for (int i = 0; i < 3; i++) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					while (true) {
						while (true) {
							Integer m = money.get();
							if (m < 20) {
								if (money.compareAndSet(m, m + 20)) {
									System.out.println("余额小于20元,充值成功,最新余额为：" + money.get() + "元");
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
						Integer m = money.get();
						if (m > 10) {
							System.out.println("大于10元");
							if (money.compareAndSet(m, m - 10)) {
								System.out.println("成功消费10元,余额：" + money.get());
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
