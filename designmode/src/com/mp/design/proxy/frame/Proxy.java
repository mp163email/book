package com.mp.design.proxy.frame;

/**
 * 代理对象
 * @author mp
 * @date 2013-9-7 下午2:47:34
 */
public class Proxy implements Subject{

	/**
	 * 持有被代理的具体的目标对象
	 */
	private RealSubject realSubject = null;
	
	/**
	 * 构造方法，传入被代理的具体的目标对象
	 * @param realSubject
	 */
	public Proxy(RealSubject realSubject){	
		this.realSubject = realSubject;
	}
	
	@Override
	public void request() {
		//在转掉具体的目标对象前，可以执行一些功能
		//转掉具体的目标对象的方法
		realSubject.request();
		//在转掉具体的目标对象后，可以执行一些功能处理
	}

}
