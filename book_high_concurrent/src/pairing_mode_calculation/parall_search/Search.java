package pairing_mode_calculation.parall_search;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 并行搜索
 * 一个数组,分拆成多个段,每个段一个线程,找到就返回他所在的下标,可能会出现多个一样的查找值,出现多个就把多个的下标返回
 * @author mp
 * @date 2016-7-22 下午5:20:40
 */
public class Search {
	
	static int [] arr = {1,2,3,4,10,6,7,8,10,10};
	
	static ExecutorService pool = Executors.newCachedThreadPool();
	
	static final int thread_num = 2;
	
//	static AtomicInteger result = new AtomicInteger(-1);//-1表示没找到结果
	
	/**
	 * 搜索方法,找到后将下标返回(弄一个静态方法,供搜索线程去调用)
	 * @author mp
	 * @date 2016-7-22 下午5:22:14
	 * @param searchValue
	 * @param beginPos
	 * @param endPos
	 * @return
	 * @Description
	 */
	public static String search (int searchValue, int beginPos, int endPos) {
		
		String ret = "";
		
		int i = 0;
		for (i = beginPos; i < endPos; i++) {
//			if (result.get() >= 0) {
//				return result.get();
//			}
			if (arr[i] == searchValue) {
				//如果设置失败,表示其他线程已经先找到了(这个地方有一个不合理的地方,就是如果要查找的这个值,有多个,那就是这个值先被谁找到就返回谁)
//				if (!result.compareAndSet(-1, i)) {
//					return result.get();
//				}
				ret += i + ",";
			}
		}
		return ret.equals("") ? "" : ret.substring(0, ret.length() - 1);
	}
	
	/**
	 * 搜索线程
	 * @author mp
	 * @date 2016-7-22 下午5:34:42
	 */
	public static class SearchTask implements Callable<String> {
		
		int begin, end, searchValue;
		
		public SearchTask (int searchValue, int begin, int end) {
			this.begin = begin;
			this.end = end;
			this.searchValue = searchValue;
		}
		
		@Override
		public String call() throws Exception {
			String re = search(searchValue, begin, end);
			return re;
		}
	}
	
	/**
	 * 主函数
	 * @author mp
	 * @date 2016-7-22 下午5:37:52
	 * @param args
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 * @Description
	 */
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		
		int searchValue = 10;
		
		int subArrSize = arr.length / thread_num;//计算每个分割后的数组的大小
		List<Future<String>> re = new ArrayList<>();
		for (int i = 0; i < arr.length; i+=subArrSize) {
			int end = i + subArrSize;
			if (end > arr.length) {
				end = arr.length;
			}
			re.add(pool.submit(new SearchTask(searchValue, i, end)));
		}
		for (Future<String> future : re) {
			if (!future.get().equals("")) {
				System.out.println("index = " + future.get());
			}
		}
		pool.shutdown();
	}
}
