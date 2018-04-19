package lock_optim_notice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.amino.ds.lockfree.LockFreeList;
import org.amino.ds.lockfree.LockFreeVector;

/**
 * Amino并发包List测试
 * 本例子并不能证明无锁List比同步的List快,反而还慢了好多好多,不知道是不是因为jdk优化的结果
 * @author mp
 * @date 2016-7-20 下午3:01:09
 */
public class NoLock_Amino_List {
	
	private static final int max_threads = 2000;//线程数量
	
	private static final int task_count = 4000;//任务数量
	
	List<Integer> list;
	
	/**
	 * 测试用线程
	 * @author mp
	 * @date 2016-7-20 下午3:04:40
	 */
	public class AccessListThread implements Runnable {
		
		protected String name;
		
		Random rand = new Random();
		
		public AccessListThread () {
			
		}
		
		public AccessListThread (String name) {
			this.name = name;
		}
		
		@Override
		public void run() {
			try {
				for (int i = 0; i < 1000; i++) {
					handleList(rand.nextInt(1000));
				}
				Thread.sleep(rand.nextInt(100));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 扩展线程池,用于统计
	 * @author mp
	 * @date 2016-7-20 下午3:26:18
	 */
	public class CounterPoolExecutor extends ThreadPoolExecutor {

		private AtomicInteger count = new AtomicInteger();
		
		public long startTime = 0;
		
		public String funcname = "";
		
		public CounterPoolExecutor(int corePoolSize, int maximumPoolSize,
				long keepAliveTime, TimeUnit unit,
				BlockingQueue<Runnable> workQueue) {
			super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
		}

		@Override
		protected void afterExecute(Runnable r, Throwable t) {
			int l = count.addAndGet(1);
			if (l == task_count) {
				System.out.println(funcname + " spend time : " + (System.currentTimeMillis() - startTime));
			}
		}
	}
	
	/**
	 * 模拟增加删除
	 * @author mp
	 * @date 2016-7-20 下午3:16:00
	 * @param index
	 * @return
	 * @Description
	 */
	public Object handleList (int index) {
		list.add(index);
//		list.remove(index % list.size());
		return null;
	}
	
	/**
	 * 初始化线程安全的LinkedList
	 * @author mp
	 * @date 2016-7-20 下午3:12:06
	 * @Description
	 */
	public void initLinkedList () {
		List<Integer> l = new ArrayList<>();
		for (int i = 0; i < 1000; i++) {
			l.add(i);
		}
		list = Collections.synchronizedList(new LinkedList<>(l));
	}
	
	/**
	 * 初始化线程安全的Vector
	 * @author mp
	 * @date 2016-7-20 下午3:13:14
	 * @Description
	 */
	public void initVector () {
		List<Integer> l = new ArrayList<>();
		for (int i = 0; i < 1000; i++) {
			l.add(i);
		}
		list = new Vector<>(l);
	}
	
	/**
	 * 初始化LockFreeList
	 * @author mp
	 * @date 2016-7-20 下午3:21:27
	 * @Description
	 */
	public void initFreeLockList () {
		list = new LockFreeList<Integer>();
		for (int i = 0; i < 1000; i++) {
			list.add(i);
		}
	}
	
	/**
	 * 初始化LockFreeVector
	 * @author mp
	 * @date 2016-7-20 下午3:23:10
	 * @Description
	 */
	public  void initFreeLockVector () {
		list = new LockFreeVector<>();
		for (int i = 0; i < 1000; i++) {
			list.add(i);
		}
	}
	
	/**
	 * 测试FreeLockVector
	 * @author mp
	 * @date 2016-7-20 下午3:55:48
	 * @throws InterruptedException
	 * @Description
	 */
	public static void testFreeLockVector () throws InterruptedException {
		NoLock_Amino_List noList = new NoLock_Amino_List();
		noList.initFreeLockVector();
		CounterPoolExecutor exe = noList.new CounterPoolExecutor(max_threads, max_threads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
		long start = System.currentTimeMillis();
		exe.startTime = start;
		exe.funcname = "FreeLockVector";
		Runnable t = noList.new AccessListThread();
		for (int i = 0; i < task_count; i++) {
			exe.submit(t);
		}
		Thread.sleep(10 * 1000);
	}
	
	/**
	 * 测试Vector
	 * @author mp
	 * @date 2016-7-20 下午3:55:01
	 * @throws InterruptedException
	 * @Description
	 */
	public static void testVector () throws InterruptedException {
		NoLock_Amino_List noList = new NoLock_Amino_List();
		noList.initVector();
		CounterPoolExecutor exe = noList.new CounterPoolExecutor(max_threads, max_threads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
		long start = System.currentTimeMillis();
		exe.startTime = start;
		exe.funcname = "Vector";
		Runnable t = noList.new AccessListThread();
		for (int i = 0; i < task_count; i++) {
			exe.submit(t);
		}
		Thread.sleep(10 * 1000);
	}
	
	/**
	 * 测试同步的LinkedList
	 * @author mp
	 * @date 2016-7-20 下午3:29:47
	 * @throws InterruptedException
	 * @Description
	 */
	public static void testSyncLinkedList () throws InterruptedException {
		NoLock_Amino_List noList = new NoLock_Amino_List();
		noList.initLinkedList();
		CounterPoolExecutor exe = noList.new CounterPoolExecutor(max_threads, max_threads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
		long start = System.currentTimeMillis();
		exe.startTime = start;
		exe.funcname = "SyncLinkedList";
		Runnable t = noList.new AccessListThread();
		for (int i = 0; i < task_count; i++) {
			exe.submit(t);
		}
		Thread.sleep(10 * 1000);
	}
	
	/**
	 * 测试LockFreeList
	 * @author mp
	 * @date 2016-7-20 下午3:53:38
	 * @throws InterruptedException
	 * @Description
	 */
	public static void testFreeLockList () throws InterruptedException {
		NoLock_Amino_List noList = new NoLock_Amino_List();
		noList.initFreeLockList();
		CounterPoolExecutor exe = noList.new CounterPoolExecutor(max_threads, max_threads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
		long start = System.currentTimeMillis();
		exe.startTime = start;
		exe.funcname = "testFreeLockList";
		Runnable t = noList.new AccessListThread();
		for (int i = 0; i < task_count; i++) {
			exe.submit(t);
		}
		Thread.sleep(10 * 1000);
	}
	
	/**
	 * 主函数
	 * @author mp
	 * @date 2016-7-20 下午3:39:00
	 * @param args
	 * @throws InterruptedException
	 * @Description
	 */
	public static void main(String[] args) throws InterruptedException {
		testFreeLockList();//运行的最慢,其他三个还行
//		testFreeLockVector();
//		
//		testSyncLinkedList();
//		testVector();
	}
}
