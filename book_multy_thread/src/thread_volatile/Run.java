package thread_volatile;

public class Run {
	public static void main(String[] args) {
		try {
			RunThread thread = new RunThread();
			thread.start();
			Thread.sleep(1000);
			thread.setRunning(false);//共享内存变量
			System.out.println("已赋值成false");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
