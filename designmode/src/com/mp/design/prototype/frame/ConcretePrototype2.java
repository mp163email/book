package com.mp.design.prototype.frame;

/**
 * 克隆的具体实现对象
 * @author mp
 * @date 2013-9-6 上午11:28:17
 */
public class ConcretePrototype2 implements Prototype{
	
	public Prototype clone(){
		//最简单的克隆，新建一个自身对象，由于没有属性，就不在赋值了
		Prototype prototype = new ConcretePrototype2();
		return prototype;
		
	}
}
