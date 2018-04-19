package com.mp.design.singleton.frame;

/**
 * 加锁的懒汉式每次都要synchronized会影响性能，用双重检查加锁
 * @author mp
 * @date 2013-9-5 下午12:27:37
 */
public class Singleton4 {

	//被volatile修饰的变量的值，将不会被本地线程缓存，所有对该变量的读写都是直接操作共享内存，从而确保多个线程能正确的处理该变量
	private volatile static Singleton4 instance = null;
	
	private Singleton4(){
		
	}
	
	public static Singleton4 getInstance(){
		
		//先检查实例是否存在，如果不存在才进入下面的同步块
		if(instance == null){
			synchronized (Singleton4.class) {
				//再次检查实例是否存在，如果不存在才真正地创建实例
				if(instance == null){
					instance = new Singleton4();
				}
			}
		}
		return instance;
	}
}
