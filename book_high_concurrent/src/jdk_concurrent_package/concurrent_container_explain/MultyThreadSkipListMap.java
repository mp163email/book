package jdk_concurrent_package.concurrent_container_explain;

import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 多线程下ConcurrentSkipListMap和Collections.synchornizedSortMap(TreeMap)和ConcurrentHashMap进行随机读性能比较
 * 用例：开10个线程,每个线程对有10万数据的map进行10万次随机读
 * 结论：ConcurrentHashMap耗时100多毫秒,  ConcurrentSkipListMap耗时200多毫秒,Collections.synchornizedSortMap(TreeMap)耗时500多毫秒
 *      但是ConcurrentHashMap不是按key排序的,如果要求按key排序的话,优先选用ConcurrentSkipListMap;如果不要求按key优先排序用ConcurrentHashMap
 * @author mp
 * @date 2016-7-14 上午11:12:08
 */
public class MultyThreadSkipListMap {
	
	/**
	 * 枚举类型
	 * @author mp
	 * @date 2016-7-14 下午2:33:35
	 */
	public  enum MapType {
		SkipListMap, SyncTreeMap, ConcuHashMap
	}
	
	/**
	 * 获取Map
	 * @author mp
	 * @date 2016-7-14 下午2:25:51
	 * @param type
	 * @return
	 * @Description
	 */
	public static Map<Integer, Integer> getMap (MapType mapType) {
		if (mapType == MapType.SyncTreeMap) {
			return Collections.synchronizedSortedMap(new TreeMap<Integer, Integer>());
		} else if (mapType == MapType.SkipListMap) {
			return new ConcurrentSkipListMap<Integer, Integer>();
		} else if (mapType == MapType.ConcuHashMap) {
			return new ConcurrentHashMap<Integer, Integer>();
		} else {
			return null;
		}
	}
	
	/**
	 * 内部静态类：随机读
	 * @author mp
	 * @date 2016-7-14 下午2:21:49
	 */
	public static class RandomRead implements Runnable {

		Map<Integer, Integer> map = null;
		
		public RandomRead (Map<Integer, Integer> map) {
			this.map = map;
		}
		
		@Override
		public void run() {
			for (int i = 0; i < 10 * 10000; i++) {
				int random = new Random().nextInt(10 * 10000);
				map.get(random);
			}
		}
	}
	
	/**
	 * 主函数
	 * @author mp
	 * @date 2016-7-14 下午2:28:29
	 * @param args
	 * @Description
	 */
	public static void main(String[] args) {
		
		//获取map
		Map<Integer, Integer> map = getMap(MapType.SyncTreeMap);
		
		//初始化map
		for (int i = 0; i < 10 * 10000; i++) {
			map.put(i, i);
		}

		//自定义线程池
		int nThread = 10;
		final long start = System.currentTimeMillis();
		ExecutorService exec = new ThreadPoolExecutor(nThread, nThread, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>()){
			@Override
			protected void terminated() {
				long end = System.currentTimeMillis();
				System.out.println("total used time is " + (end - start) + "ms");
			}
		};
		
		//执行任务
		for (int i = 0; i < nThread; i++) {
			RandomRead command = new RandomRead(map);
			exec.execute(command);
		}
		exec.shutdown();
		
	}
}
