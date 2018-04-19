package pairing_base.thread_sleep_stop;


/**
 * 睡眠中消亡
 * @author mp
 * @date 2016-7-11 上午10:27:33
 */
public class Thread_Sleep_Stop {
	
	public static void main(String[] args) throws Exception{
		
		Thread t1 = new Thread(){
			@Override
			public void run() {
				while (true) {
					System.out.println("Start " + Thread.currentThread().isInterrupted());
					if (Thread.currentThread().isInterrupted()) {
						System.out.println("Break!");
						break;
					}
					try {
						System.out.println("Start Sleep ");
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						System.out.println("Interruted! " + Thread.currentThread().isInterrupted());
						Thread.currentThread().interrupt();//这句话必须有,因为外部interrupt后,睡眠被打断,catch处理后，会清除打断状态值,又变成false,只能再重新打断一次
					}
					Thread.yield();
				}
			}
		};
		t1.start();
		
		Thread.sleep(1000);
		t1.interrupt();
	}
}
