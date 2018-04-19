package pairing_base.thread_good_suspend;

/**
 * 用wait-notify实现suspend-resume
 * @author mp
 * @date 2016-7-11 上午10:57:10
 */
public class GoodSuspend {
	
	/**
	 * 静态内部类
	 * @author mp
	 * @date 2016-7-11 上午11:11:56
	 */
	public static class ChangeObjectThread extends Thread {
		
		volatile private boolean suspendme = false;//如果以直接访问suspendme属性方式,volatile是必须的,如果用方法调用,可以不加（volatile使线程之间,变量值可见,包括main线程）
		
		public void suspendMe () {
			suspendme = true;
		}
		
		//恢复-notify控制
		public void resumeMe () {
			suspendme = false;
			synchronized (this) {
				notify();
			}
		}

		@Override
		public void run() {
			while (true) {
				
				//暂停-wait控制
				synchronized (this) {
					while (suspendme) {
						try {
							wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				
				//默认执行的逻辑
				try {
					Thread.sleep(100);
					System.out.println("in ChangeObjectThread");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public static void main(String[] args) throws InterruptedException {
		ChangeObjectThread t1 = new ChangeObjectThread();
		t1.start();
		Thread.sleep(1000);
		
		t1.suspendMe();
		System.out.println("t1 suspend 5 sec");
		Thread.sleep(5000);
		System.out.println("t1 resume");
		t1.resumeMe();
	}
	
}
