package com.mp.design.singleton.sample;

import java.util.HashMap;
import java.util.Map;

/**
 * 简单演示如何扩展单例模式，控制实例数目为3个
 * 
 * @author mp
 * @date 2013-9-5 下午12:01:18
 */
public class SingletonExtend {

	/**
	 * 定义一个缺省的key值的前缀
	 */
	private final static String DEFAULT_KEY = "Cache";

	/**
	 * 缓存实例的容器
	 */
	private static Map<String, SingletonExtend> map = new HashMap<String, SingletonExtend>();

	/**
	 * 用来记录当前使用第几个实例，到了控制的最大数目看，就返回从1开始
	 */
	private static int num = 1;

	/**
	 * 定义控制实例的最大数目
	 */
	private final static int NUM_MAX = 3;

	/**
	 * 私有的构造函数
	 */
	private SingletonExtend() {

	}

	public static SingletonExtend getInstance() {

		String key = DEFAULT_KEY + num;
		
		SingletonExtend singletonExtend = map.get(key);
		if (singletonExtend == null) {
			singletonExtend = new SingletonExtend();
			map.put(key, singletonExtend);
		}
		num++;

		if (num > NUM_MAX) {
			num = 1;
		}

		return singletonExtend;
	}
}
