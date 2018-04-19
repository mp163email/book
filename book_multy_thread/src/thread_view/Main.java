package thread_view;

import java.util.concurrent.TimeUnit;

public class Main {
	public static void main(String[] args) throws Exception{
		
		View view = new View();//对象变量==共享内存变量?
		
		ThreadA threadA = new ThreadA(view);
		threadA.start();
		
		TimeUnit.MILLISECONDS.sleep(3000);
		
		ThreadB threadB = new ThreadB(view);
		threadB.start();
		
//		view.flag = true;//共享内存变量
	}
}
