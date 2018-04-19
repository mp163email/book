package com.mp.design.factorymethod.frame;

/**
 * 具体的创建器实现对象
 * @author mp
 * @date 2013-9-5 下午2:38:50
 */
public class ConcreteCreator extends Creator{

	@Override
	protected Product factoryMethod() {
		//重定义工厂方法，返回一个具体的Product对象
		return new ConcreateProduct();
	}
	
}
