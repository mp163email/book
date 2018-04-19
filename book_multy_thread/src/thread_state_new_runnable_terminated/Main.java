package thread_state_new_runnable_terminated;

public class Main {
	public static void main(String[] args) throws Exception {
		MyThread myThread = new MyThread();
		System.out.println("main 方法start前的状态： " + myThread.getState());
		Thread.sleep(1000);
		myThread.start();
		Thread.sleep(1000);
		System.out.println("main 方法start后的状态： " + myThread.getState());
	}
}
