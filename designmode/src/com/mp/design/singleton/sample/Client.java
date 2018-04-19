package com.mp.design.singleton.sample;

/**
 * 利用缓存扩展单例模式，控制实例数目为3个
 * @author mp
 * @date 2013-9-5 下午12:16:18
 */
public class Client {
	
	public static void main(String[] args) {
		
		SingletonExtend se1 = SingletonExtend.getInstance();
		SingletonExtend se2 = SingletonExtend.getInstance();
		SingletonExtend se3 = SingletonExtend.getInstance();
		SingletonExtend se4 = SingletonExtend.getInstance();
		SingletonExtend se5 = SingletonExtend.getInstance();
		SingletonExtend se6 = SingletonExtend.getInstance();
		
		System.out.println("se1="+se1);
		System.out.println("se2="+se2);
		System.out.println("se3="+se3);
		System.out.println("se4="+se4);
		System.out.println("se5="+se5);
		System.out.println("se6="+se6);
	}
}
