package com.mp.design.visitor.frame;

/**
 * 访问者的接口
 * @author mp
 * @date 2013-9-8 下午1:51:06
 */
public interface Visitor {

	/**
	 * 访问元素A,相当于给A元素添加访问者的功能
	 * @author mp
	 * @date 2013-9-8 下午1:51:48
	 * @param elementA
	 * @Description
	 */
	public void visitConcreteElementA(ConcreteElementA elementA);

	/**
	 * 访问元素B,相当于给B元素添加访问者的功能
	 * @author mp
	 * @date 2013-9-8 下午1:52:14
	 * @param elementB
	 * @Description
	 */
	public void visitConcreteElementB(ConcreteElementB elementB);

}
