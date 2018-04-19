package com.mp.design.flyweight.frame;

import java.util.HashMap;
import java.util.Map;

/**
 * 享元工厂
 * 
 * @author mp
 * @date 2013-9-8 下午1:01:03
 */
public class FlyweightFactory {

	/**
	 * 缓存多个Flyweight对象，这里只是示意一下
	 */
	private Map<String, Flyweight> fsMap = new HashMap<String, Flyweight>();

	/**
	 * 获取key对应的享元对象
	 * 
	 * @author mp
	 * @date 2013-9-8 下午1:02:45
	 * @param key
	 * @return
	 * @Description
	 */
	public Flyweight getFlyweight(String key) {
		// 1.先从缓存中查找，是否存在key对应的Flyweight对象
		Flyweight f = fsMap.get(key);

		// 2.如果存在，就返回相对应的Flyweight对象
		if (f == null) {
			f = new ConcreteFlyweight(key);
			fsMap.put(key, f);
		}
		
		return f;
	}
}
