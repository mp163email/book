package com.mp.design.mediator.frame;

/**
 * 具体的同事类B
 * @author mp
 * @date 2013-9-7 上午8:44:28
 */
public class ConcreteColleagueB extends Colleague{

	public ConcreteColleagueB(Mediator mediator) {
		super(mediator);
	}

	/**
	 * 示意方法，执行某些业务功能
	 * @author mp
	 * @date 2013-9-7 上午8:47:12
	 * @Description
	 */
	public void someOperation(){
		getMediator().changed(this);
	}
}
