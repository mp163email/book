package com.mp.design.iterator.sample.design;


/**
 * 获取访问聚合的接口
 * @author mp
 * @date 2013-9-7 下午5:45:08
 */
public abstract class Aggregate {
	
	/**
	 * 工厂方法，创建相应迭代器对象的接口
	 * @author mp
	 * @date 2013-9-7 下午5:46:00
	 * @return
	 * @Description
	 */
	public abstract Iterator createIterator();
}
