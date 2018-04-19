package thread_daemon;

public class Main {
	public static void main(String[] args) throws Exception {
		MyThread myThread = new MyThread();
		myThread.setDaemon(true);//守护线程：这个进程里没有用户线程了（main也属于用户线程），他就自行销毁关闭
		myThread.start();
		Thread.sleep(5000);
		System.out.println("我离开Thread对象也不在打印了，也就是停止了");
		MyThread myThread1 = new MyThread();
		myThread1.start();
		Thread.sleep(5000);
		myThread1.interrupt();
	}
}
