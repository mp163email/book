package com.mp.design.mediator.frame;

/**
 * 具体的中介者实现
 * @author mp
 * @date 2013-9-7 上午8:49:27
 */
public class ConcreteMediator implements Mediator{

	/**
	 * 持有并维护同事A
	 */
	private ConcreteColleagueA colleagueA;
	
	/**
	 * 持有并维护同事B
	 */
	private ConcreteColleagueB colleagueB;

	/**
	 * 设置中介者需要了解并维护的同事A对象
	 * @author mp
	 * @date 2013-9-7 上午8:53:26
	 * @param colleagueA
	 * @Description
	 */
	public void setColleagueA(ConcreteColleagueA colleagueA) {
		this.colleagueA = colleagueA;
	}

	/**
	 * 设置中介者需要了解并维护的同事B对象
	 * @author mp
	 * @date 2013-9-7 上午8:53:55
	 * @param colleagueB
	 * @Description
	 */
	public void setColleagueB(ConcreteColleagueB colleagueB) {
		this.colleagueB = colleagueB;
	}

	@Override
	public void changed(Colleague colleague) {
		//某个同事类发生变化了，通常和其他同事类交互
		//具体协调相应的同事对象来实现协作行为
	}

}
