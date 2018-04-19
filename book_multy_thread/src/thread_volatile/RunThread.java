package thread_volatile;

public class RunThread extends Thread {
	private boolean isRunning = true;//线程内变量

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	@Override
	public void run() {
		System.out.println("进入run了");
		while (isRunning) {
			
		}
		System.out.println("线程被停止了");
	}
	
}
