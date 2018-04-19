package com.mp.design.composite.frame;

/**
 * 抽象的组件对象
 * @author mp
 * @date 2013-9-8 上午11:11:27
 */
public abstract class Component {

	/**
	 * 示意方法，子组件对象可能有的功能方法
	 * @author mp
	 * @date 2013-9-8 上午11:13:06
	 * @Description
	 */
	public abstract void someOperation();
	
	/**
	 * 向组合对象中加入组件对象
	 * @author mp
	 * @date 2013-9-8 上午11:13:22
	 * @param child
	 * @Description
	 */
	public void addChild(Component child){
		throw new UnsupportedOperationException("对象不支持这个功能");
	}
	
	/**
	 * 从组合对象中移除某个组件对象
	 * @author mp
	 * @date 2013-9-8 上午11:14:59
	 * @param child
	 * @Description
	 */
	public void removeChild(Component child){
		throw new UnsupportedOperationException("对象不支持这个功能");
	}
	
	/**
	 * 返回某个索引对应的组件对象
	 * @author mp
	 * @date 2013-9-8 上午11:16:03
	 * @param index
	 * @return
	 * @Description
	 */
	public Component getChildren(int index){
		throw new UnsupportedOperationException("对象不支持这个功能");
	}
}
