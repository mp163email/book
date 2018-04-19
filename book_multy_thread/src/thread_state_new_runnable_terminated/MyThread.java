package thread_state_new_runnable_terminated;

public class MyThread extends Thread {
	
	public MyThread () {
		System.out.println("构造方法里的状态：" + Thread.currentThread().getName() + " " + Thread.currentThread().getState());
	}
	
	public void run () {
		System.out.println("Run方法里的状态：" + Thread.currentThread().getName() + " " + Thread.currentThread().getState());
	}
}
