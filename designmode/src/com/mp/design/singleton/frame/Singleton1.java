package com.mp.design.singleton.frame;

/**
 * 懒汉式单例实现的示例
 * @author mp
 * @date 2013-9-5 上午11:24:57
 */
public class Singleton1 {
	
	private static Singleton1 uniqueInstance = null;
	
	/**
	 * 私有的构造方法
	 */
	private Singleton1(){

	}
	
	/**
	 * 定义一个方法莱维客户端提供类实例
	 * @author mp
	 * @date 2013-9-5 上午11:27:07
	 * @return
	 * @Description 不加同步的懒汉式是线程不安全的
	 */
	public static synchronized Singleton1 getInstance(){
		if(uniqueInstance == null){
			uniqueInstance = new Singleton1();
		}
		return uniqueInstance;
	}
	
	/**
	 * 示意方法，单例可以有自己的操作
	 * @author mp
	 * @date 2013-9-5 上午11:28:27
	 * @Description
	 */
	public void singletonOperate(){
		
	}
	
	/**
	 * 示意方法，单例可以有自己的属性
	 */
	private String singetonData;
	
}
