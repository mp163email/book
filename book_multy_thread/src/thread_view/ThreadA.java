package thread_view;

public class ThreadA extends Thread {
	
	View view = null;
	
	public ThreadA (View view) {
		this.view = view;
	}

	@Override
	public void run() {
		view.viewFlag();
	}
}
