package com.mp.design.visitor.frame;

/**
 * 具体元素的实现对象
 * @author mp
 * @date 2013-9-8 下午1:53:52
 */
public class ConcreteElementA extends Element{

	@Override
	public void accept(Visitor visitor) {
		//回调访问者对象的相应方法
		visitor.visitConcreteElementA(this);
	}
	
	/**
	 * 示意方法，表示元素已经有的方法
	 * @author mp
	 * @date 2013-9-8 下午1:54:52
	 * @Description
	 */
	public void operationA(){
		//已有功能
	}

}
