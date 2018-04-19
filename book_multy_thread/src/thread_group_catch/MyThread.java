package thread_group_catch;

public class MyThread extends Thread {
	
	private String numStr;
	
	public MyThread (ThreadGroup group, String threadName, String numStr) {
		super(group, threadName);
		this.numStr = numStr;
	}

	@Override
	public void run() {
//		try {//***********如果加上了try-catch,自定义线程组内的uncaughtException不会被执行**任何地方有try-catch都不行********
			Integer.valueOf(numStr);
			while (true) {
				if (!Thread.interrupted()) {//********Thread.interrupted()打断变成true后,会再次初始化值变成false,也就是说如果再次调用,值是false***********
					System.out.println("无限循环中" + Thread.currentThread().getName());
				} else {
					System.out.println("interrupt !");
					break;
				}
			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
}
