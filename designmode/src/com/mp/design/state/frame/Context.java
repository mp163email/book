package com.mp.design.state.frame;

/**
 * 定义客户端感兴趣的接口，通常会维护一个State类型的对象实例
 * @author mp
 * @date 2013-9-8 下午12:23:23
 */
public class Context {
	
	/**
	 * 持有一个State类型的对象实例
	 */
	private State state;

	public void setState(State state) {
		this.state = state;
	}
	
	public void request(String sampleParameter){
		//在处理中，会转掉state来处理
		state.handle(sampleParameter);
	}
}
