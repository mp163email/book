package com.mp.design.flyweight.frame;

/**
 * 不需要共享的flyweight对象
 * @author mp
 * @date 2013-9-8 下午12:59:07
 */
public class UnsharedConcreteFlyweight implements Flyweight{

	/**
	 * 示意，描述对象的状态
	 */
	private String allState;
	
	@Override
	public void operation(String extrnState) {
		//具体的功能处理
	}

}
