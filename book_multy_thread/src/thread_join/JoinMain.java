package thread_join;

public class JoinMain {
	public static void main(String[] args) throws Exception{
		ThreadX threadX = new ThreadX();
		threadX.start();
		threadX.join();
		System.out.println(Thread.currentThread().getName() + " 线程结束");
	}
}
