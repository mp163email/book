package com.mp.design.observer.frame;

/**
 * 具体的目标对象，负责把有关状态润如到相应的观察着对象，并在自己状态发生改变时，通知各个观察者。
 * @author mp
 * @date 2013-9-7 下午3:45:52
 */
public class ConcreteSubject extends Subject{

	/**
	 * 示意，目标对象的状态
	 */
	private String subjectState;

	public String getSubjectState() {
		return subjectState;
	}

	public void setSubjectState(String subjectState) {
		this.subjectState = subjectState;
		this.notifyObervers();
	}
	
}
