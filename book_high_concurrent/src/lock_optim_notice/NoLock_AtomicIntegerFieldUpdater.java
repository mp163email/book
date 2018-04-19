package lock_optim_notice;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

/**
 * 例子模拟给某个人投票，投票人里边有一个字段记录投得的票数
 * 让普通变量也享受原子操作
 * 多线程修改某个对象的int类型的字段
 * 1.要求这个必须是public的,因为用的反射弄的，private的找不到
 * 2.要求这个变量必须是volatile的，因为多线程访问要有可见性
 * 3.这个变量不能是static的，因为他是通过unsafe的偏移量直接修改变量的值,这个过程不支持静态变量
 * @author mp
 * @date 2016-7-18 下午3:43:52
 */
public class NoLock_AtomicIntegerFieldUpdater {
	
	/**
	 * 对象里的int字段,享受原子操作
	 */
	public static AtomicIntegerFieldUpdater<Candidate> scoreUpdater = AtomicIntegerFieldUpdater.newUpdater(Candidate.class, "score");
	
	/**
	 * 为了验证结果
	 */
	public static AtomicInteger allScore = new AtomicInteger();
	
	/**
	 * 候选人类
	 * 注意score是volatile的,而且不是私有的
	 * @author mp
	 * @date 2016-7-18 下午4:21:34
	 */
	public static class Candidate {
		int id;
		volatile int score;
	}
	
	/**
	 * 主函数
	 * @author mp
	 * @date 2016-7-18 下午4:25:39
	 * @param args
	 * @throws InterruptedException 
	 * @Description
	 */
	public static void main(String[] args) throws InterruptedException {
		final Candidate stu = new Candidate();
		Thread [] t = new Thread[10 * 1000];
		for (int i = 0; i < t.length; i++) {
			t[i] = new Thread(){
				@Override
				public void run() {
					if (Math.random() > 0.4) {
						scoreUpdater.incrementAndGet(stu);//因为scoreUpdater在初始化的时候,指明了字段score,所以这里的incrementAndGet是操作的score这个字段
						allScore.incrementAndGet();
					}
				}
			};
			
			t[i].start();
		}
		for (int i = 0; i < t.length; i++) {
			t[i].join();
		}
		System.out.println("score=" + stu.score);
		System.out.println("allScore=" + allScore.get());
	}
	
}
