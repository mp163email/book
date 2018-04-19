package thread_catch_handler;


public class MyThread extends Thread {

	@SuppressWarnings("null")
	@Override
	public void run() {
		String str = null;
		System.out.println(str.hashCode());
	}
	
}
