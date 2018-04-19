package thread_view;

public class ThreadB extends Thread {
	
	View view = null;
	
	public ThreadB (View view) {
		this.view = view;
	}

	@Override
	public void run() {
		view.update();
	}
}
