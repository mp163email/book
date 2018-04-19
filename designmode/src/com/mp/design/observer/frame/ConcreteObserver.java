package com.mp.design.observer.frame;

/**
 * 具体观察者对象，实现更新的方法，使自身的状态和目标的状态保持一致
 * @author mp
 * @date 2013-9-7 下午3:49:15
 */
public class ConcreteObserver implements Observer{

	private String observerState;

	@Override
	public void update(Subject subject) {
		//具体的更新实现
		//这里可能需要更新观察者的状态，使其与目标的状态保持一致
		observerState = ((ConcreteSubject)subject).getSubjectState();
	}
	
}
