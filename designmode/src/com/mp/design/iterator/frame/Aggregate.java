package com.mp.design.iterator.frame;

public abstract class Aggregate {
	
	/**
	 * 工厂方法，创建相应迭代器对象的接口
	 * @author mp
	 * @date 2013-9-7 下午5:07:21
	 * @return
	 * @Description
	 */
	public abstract Iterator createIterator();
}
