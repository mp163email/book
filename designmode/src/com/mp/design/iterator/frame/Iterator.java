package com.mp.design.iterator.frame;

/**
 * 迭代器接口，定义访问和遍历元素的操作
 * @author mp
 * @date 2013-9-7 下午5:02:36
 */
public interface Iterator {

	/**
	 * 移动到聚合对象的第一个位置
	 * @author mp
	 * @date 2013-9-7 下午5:03:10
	 * @Description
	 */
	public void first();
	
	/**
	 * 移动到聚合对象的下一个位置
	 * @author mp
	 * @date 2013-9-7 下午5:03:29
	 * @Description
	 */
	public void next();
	
	/**
	 * 获取当前元素
	 * @author mp
	 * @date 2013-9-7 下午5:04:26
	 * @return
	 * @Description
	 */
	public Object currentItem();
	
	/**
	 * 是否已经移动到聚合对象的最后一个位置
	 * @author mp
	 * @date 2013-9-7 下午5:03:51
	 * @return
	 * @Description
	 */
	public boolean isDone();
	
}
