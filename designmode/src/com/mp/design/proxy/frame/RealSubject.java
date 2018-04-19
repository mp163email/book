package com.mp.design.proxy.frame;

/**
 * 具体的目标对象，是真正被代理的对象
 * @author mp
 * @date 2013-9-7 下午2:46:28
 */
public class RealSubject implements Subject{

	@Override
	public void request() {
		//执行具体的功能处理
	}
}
