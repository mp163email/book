package com.mp.design.abstractfactory.frame;

public class ConcreateFactory2 implements AbstracFactory{

	@Override
	public AbstractProductA createProductA() {
		return new ProductA2();
	}

	@Override
	public AbstractProductB createProductB() {
		return new ProductB2();
	}
	
}
