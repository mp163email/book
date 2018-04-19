package thread_group_catch;

public class MyThreadGroup extends ThreadGroup {

	public MyThreadGroup(String name) {
		super(name);
	}

	/**
	 * 当组内有线程发生异常时,就会调这个方法,参数t就是发生异常的线程
	 */
	@Override
	public void uncaughtException(Thread t, Throwable e) {
		System.out.println("线程 " + t.getName() + " 发生异常");
		this.interrupt();//线程组中断（组内所有线程都会中断）
	}
	
}
