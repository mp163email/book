package com.mp.design.strategy.frame;

/**
 * 上下文对象，通常会持有一个具体的策略对象
 * @author mp
 * @date 2013-9-8 下午12:09:53
 */
public class Context {

	/**
	 * 持有一个具体的策略对象
	 */
	private Strategy strategy;
	
	public Context(Strategy strategy){
		this.strategy = strategy;
	}
	
	/**
	 * 上下文对客户端提供接口，可以有参数和返回值
	 * @author mp
	 * @date 2013-9-8 下午12:14:27
	 * @Description
	 */
	public void contextInterface(){
		
		//通常会转掉具体的策略对象进行算法运算
		strategy.algorithmInterface();
	}
	
}
