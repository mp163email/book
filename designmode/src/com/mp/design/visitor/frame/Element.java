package com.mp.design.visitor.frame;

/**
 * 被访问的元素的接口
 * @author mp
 * @date 2013-9-8 下午1:52:42
 */
public abstract class Element {

	/**
	 * 接收访问者的访问
	 * @author mp
	 * @date 2013-9-8 下午1:53:18
	 * @param visitor
	 * @Description
	 */
	public abstract void accept(Visitor visitor);
}
