package com.mp.design.flyweight.frame;

/**
 * 享元接口，通过这个接口享元可以接受并作用于外部状态
 * @author mp
 * @date 2013-9-8 下午12:54:00
 */
public interface Flyweight {

	/**
	 * 示意操作，传入外部状态
	 * @author mp
	 * @date 2013-9-8 下午12:55:43
	 * @param extrnState
	 * @Description
	 */
	public void operation(String extrnState);
}
