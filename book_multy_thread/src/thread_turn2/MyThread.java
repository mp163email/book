package thread_turn2;

public class MyThread extends Thread {

	private Object lock;//锁
	private String showChar;//打印的字符
	private int showNumPosition;//字符下代表的位置
	private int printCount = 0;//打印了多少次
	volatile private static int addNumber = 1;//初始偏移量    为什么这里是静态的
	
	public MyThread (Object lock, String showChar, int showNumPosition) {
		super();
		this.lock = lock;
		this.showChar = showChar;
		this.showNumPosition = showNumPosition;
	}
	
	@Override
	public void run() {
		try {
			synchronized (lock) {
				while (true) {
					if (addNumber % 4 == showNumPosition) {
						System.out.println("ThreadName=" + Thread.currentThread().getName() + " runCount=" + addNumber + " " + showChar);
						lock.notifyAll();
						addNumber ++;
						printCount ++;
						if (printCount == 4) {//每个线程打印3次就退出
							break;
						}
					} else {
						lock.wait();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
