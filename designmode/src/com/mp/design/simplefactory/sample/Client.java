package com.mp.design.simplefactory.sample;

/**
 * 从配置文件中获取实例的例子
 * @author mp
 * @date 2013-9-4 下午6:15:42
 */
public class Client {
	
	public static void main(String[] args) {
		
		Api api = Factory.instance().createApi("ImpA");
		api.operate("hello");
		
		api = Factory.instance().createApi("ImpB");
		api.operate("hello");
	}
	
}
