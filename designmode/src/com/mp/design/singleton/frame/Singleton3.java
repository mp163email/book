package com.mp.design.singleton.frame;

/**
 * 不加同步的懒汉式单例示例
 * @author mp
 * @date 2013-9-5 上午11:45:10
 */
public class Singleton3 {

	/**
	 * 类级的内部类，也就是静态的成员是内部类，改内部类的实例与外部类的实例没有绑定关系，而且只有调用到时才会装载，从而实现了延迟加载
	 * @author mp
	 * @date 2013-9-5 上午11:47:45
	 */
	private static class SingletonHolder{
		
		/**
		 * 静态初始化器，由JVM来保证线程安全 
		 */
		private static Singleton3 instance = new Singleton3();
	}
	
	/**
	 * 私有的构造方法
	 */
	private Singleton3(){
		
	}
	
	public static Singleton3 getInstance(){
		return SingletonHolder.instance;
	}
	
}
