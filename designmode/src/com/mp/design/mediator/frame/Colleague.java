package com.mp.design.mediator.frame;

/**
 * 同事类的抽象父类
 * @author mp
 * @date 2013-9-7 上午8:34:29
 */
public abstract class Colleague {
	
	/**
	 * 持有中介者对象，每一个同事类都知道他的中介者对象
	 */
	private Mediator mediator;
	
	public Colleague(Mediator mediator){
		this.mediator = mediator;
	}
	
	/**
	 * 获取同事类对应中介者对象
	 * @author mp
	 * @date 2013-9-7 上午8:37:39
	 * @return
	 * @Description
	 */
	public Mediator getMediator(){
		return mediator;
	}
}
