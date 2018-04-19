package com.mp.design.abstractfactory.frame;

public class Client {
	
	public static void main(String[] args) {
		//创建抽象工厂对象
		AbstracFactory abstracFactory = new ConcreateFactory1();
		abstracFactory.createProductA();
		abstracFactory.createProductB();
	}
}
