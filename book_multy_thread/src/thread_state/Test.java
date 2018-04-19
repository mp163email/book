package thread_state;

public class Test {
	/**
	 * 线程一共有六种状态,可以通过线程对象的getState()方法获得此线程的状态
	 * 1.new: 线程创建了,还没执行start()方法
	 * 2.runnable: 调用start()方法后,此时线程可能正在运行,也可能没有运行(2-1:sleep方法到时间了  2-2:阻塞I/O执行完毕  2-3:等半天,终于得到了锁   2-4: wait、等待某个线程通知,终于等到了 2-5：suspend状态的线程被resume)
	 * 3.blocked:   3-1:阻塞I/O  3-2:等锁   3-3: suspend
	 * 4.waiting: wait或join方法后
	 * 5.timed_waiting: sleep或wait(毫秒数)或join(毫秒数)
	 * 6.terminated: 销毁
	 */
	
	/**
	 * 释放锁的三种情况
	 * 1. 走完同步代码块
	 * 2. 走同步代码块的时候,发生异常
	 * 3. wait()方法后
	 */
}
