package com.mp.design.singleton.frame;

/**
 * 饿汉式单例实现的实例
 * @author mp
 * @date 2013-9-5 上午11:29:39
 */
public class Singleton2 {

	private static Singleton2 uniqueInstance = new Singleton2();
	
	/**
	 * 私有的构造方法
	 */
	private Singleton2(){
 
	}
	
	/**
	 * 定义一个方法莱维客户端提供类实例
	 * @author mp
	 * @date 2013-9-5 上午11:27:07
	 * @return
	 * @Description
	 */
	public static Singleton2 getInstance(){
		return uniqueInstance;
	}
	
}
