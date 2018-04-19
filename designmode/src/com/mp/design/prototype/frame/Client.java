package com.mp.design.prototype.frame;

public class Client {
	
	/**
	 * 持有需要使用的原型接口对象
	 */
	private Prototype prototype;
	
	
	public Client(Prototype prototype){
		this.prototype = prototype;
	}
	
	/**
	 * 示意方法，执行某个功能操作
	 * @author mp
	 * @date 2013-9-6 上午11:32:57
	 * @Description
	 */
	public void operation(){
		//需要创建原型接口的对象
		Prototype newPrototype = prototype.clone();
		//后续操作
	}
}
