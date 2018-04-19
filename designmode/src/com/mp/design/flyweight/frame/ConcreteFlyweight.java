package com.mp.design.flyweight.frame;

/**
 * 享元对象
 * @author mp
 * @date 2013-9-8 下午12:56:49
 */
public class ConcreteFlyweight implements Flyweight{
	
	/**
	 * 内部状态
	 */
	private String intrinsicState;
	
	public ConcreteFlyweight(String intrinsicState){
		this.intrinsicState = intrinsicState;
	}

	@Override
	public void operation(String extrnState) {
		//具体的功能处理，可能会用到享元内部，外部状态
	}

}
