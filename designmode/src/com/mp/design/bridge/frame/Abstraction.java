package com.mp.design.bridge.frame;

/**
 * 定义抽象部分的接口
 * @author mp
 * @date 2013-9-8 下午1:42:59
 */
public abstract class Abstraction {

	/**
	 * 持有一个实现部分的对象
	 */
	protected Implementor impl;
	
	public Abstraction(Implementor impl){
		this.impl = impl;
	}
	
	/**
	 * 示例操作，实现一定的功能，可能需要转掉实现部分的具体实现方法
	 * @author mp
	 * @date 2013-9-8 下午1:44:15
	 * @Description
	 */
	public void operation(){
		impl.operationImpl();
	}
}
