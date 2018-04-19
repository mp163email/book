package jdk_concurrent_package.concurrent_container_explain;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * ConcurrentSkipListMap和TreeMap一样默认按Key对数值类型排序,但对String类型不准确
 * 前者是线程安全的后者是线程不安全的
 * 有序和排序是两个不同的概念
 * @author mp
 * @date 2016-7-14 下午1:06:54
 */
public class SkipListMapSort {
	public static void main(String[] args) {
		System.out.println("=====skipListMap对Int自动按Key排序=====");
		skipListMapIntSort();
		System.out.println("=====skipListMap对String按Key排序不准确=====");
		skipListMapStringSort();
		System.out.println("======TreeMap对Int类型自动按Key排序=====");
		treeMapIntSort();
		System.out.println("======TreeMap对String按Key排序一样不准=====");
		treeMapStringSort();
		System.out.println("======LinkedHashMap能保证有序,和排序不是一个概念=====");
		linkedHashMapOrder();
		System.out.println("======HashMap输出是无序的=====");
		hashMapNoOrder();
	}
	
	/**
	 * 跳表Map对Int类型自动按Key排序
	 * @author mp
	 * @date 2016-7-14 上午11:58:06
	 * @Description
	 */
	public static void skipListMapIntSort () {
		ConcurrentSkipListMap<Integer, Integer> map = new ConcurrentSkipListMap<>();
		map.put(3, 3);
		map.put(10, 10);
		map.put(2, 2);
		map.put(5, 5);
		for (Entry<Integer, Integer> entry: map.entrySet()) {
			System.out.println(entry.getKey());
		}
	}
	
	/**
	 * 跳表Map对String类型的key排序不准
	 * @author mp
	 * @date 2016-7-14 下午1:03:06
	 * @Description
	 */
	public static void skipListMapStringSort () {
		ConcurrentSkipListMap<String, Integer> map = new ConcurrentSkipListMap<>();
		map.put("3", 3);
		map.put("10", 1);
		map.put("2", 2);
		map.put("5", 5);
		for (Entry<String, Integer> entry: map.entrySet()) {
			System.out.println(entry.getKey());
		}
	}
	
	/**
	 * TreeMap对Int类型自动按Key排序
	 * @author mp
	 * @date 2016-7-14 上午11:59:32
	 * @Description
	 */
	public static void treeMapIntSort () {
		TreeMap<Integer, Integer> map = new TreeMap<>();
		map.put(3, 3);
		map.put(1, 1);
		map.put(2, 2);
		map.put(5, 5);
		for (Entry<Integer, Integer> entry: map.entrySet()) {
			System.out.println(entry.getKey());
		}
	}
	
	/**
	 * TreeMap对String类型按Key排序
	 * @author mp
	 * @date 2016-7-14 下午1:05:31
	 * @Description
	 */
	public static void treeMapStringSort () {
		ConcurrentSkipListMap<String, Integer> map = new ConcurrentSkipListMap<>();
		map.put("3", 3);
		map.put("10", 1);
		map.put("2", 2);
		map.put("5", 5);
		for (Entry<String, Integer> entry: map.entrySet()) {
			System.out.println(entry.getKey());
		}
	}
	
	/**
	 * LinkedHashMap保证的是有序,而非排序
	 * @author mp
	 * @date 2016-7-14 下午1:53:18
	 * @Description
	 */
	public static void linkedHashMapOrder () {
		LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
		map.put("3", 3);
		map.put("10", 1);
		map.put("2", 2);
		map.put("5", 5);
		for (Entry<String, Integer> entry: map.entrySet()) {
			System.out.println(entry.getKey());
		}
	}
	
	/**
	 * HashMap输出是无序的
	 * @author mp
	 * @date 2016-7-14 下午1:54:16
	 * @Description
	 */
	public static void hashMapNoOrder () {
		Map<String, Integer> map = new HashMap<>();
		map.put("3", 3);
		map.put("10", 1);
		map.put("2", 2);
		map.put("5", 5);
		for (Entry<String, Integer> entry: map.entrySet()) {
			System.out.println(entry.getKey());
		}
	}
}
