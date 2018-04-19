package com.mp.design.templatemethod.frame;

/**
 * 定义模板方法，原语操作等的抽象类
 * @author mp
 * @date 2013-9-8 上午11:40:18
 */
public abstract class AbstractClass {
	
	/**
	 * 原语操作1，所谓原语操作就是抽象的操作，必须要由子类提供实现
	 * @author mp
	 * @date 2013-9-8 上午11:41:24
	 * @Description
	 */
	public abstract void doPrimitiveOperation1();
	
	/**
	 * 原语操作2
	 * @author mp
	 * @date 2013-9-8 上午11:41:24
	 * @Description
	 */
	public abstract void doPrimitiveOperation2();
	
	/**
	 * 模板方法，定义算法骨架
	 * @author mp
	 * @date 2013-9-8 上午11:42:46
	 * @Description
	 */
	public final void templateMoethod(){
		doPrimitiveOperation1();
		doPrimitiveOperation2();
	}
}
