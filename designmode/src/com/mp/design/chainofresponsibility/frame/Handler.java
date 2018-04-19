package com.mp.design.chainofresponsibility.frame;

/**
 * 职责的接口，也就是处理请求的接口
 * @author mp
 * @date 2013-9-8 下午1:32:00
 */
public abstract class Handler {
	
	/**
	 * 持有后继的职责对象
	 */
	protected Handler successor;

	/**
	 * 设置后继的职责对象
	 * @author mp
	 * @date 2013-9-8 下午1:33:12
	 * @param successor
	 * @Description
	 */
	public void setSuccessor(Handler successor) {
		this.successor = successor;
	}
	
	public abstract void handleRequest();
}
