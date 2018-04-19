package com.mp.design.adapter.frame;

/**
 * 使用适配器的客户端
 * @author mp
 * @date 2013-9-4 下午3:36:27
 */
public class Client {
	
	public static void main(String[] args) {
	
		//创建需要被适配的对象
		Adaptee adaptee = new Adaptee();
		
		//创建客户端需要调用的接口对象
		Target target = new Adapter(adaptee);
		
		//请求处理
		target.request();
	}
}
