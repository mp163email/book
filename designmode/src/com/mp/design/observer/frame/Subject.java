package com.mp.design.observer.frame;

import java.util.ArrayList;
import java.util.List;

/**
 * 目标对象，它知道观察他的观察者，并提供注册和删除观察者的接口
 * @author mp
 * @date 2013-9-7 下午3:39:55
 */
public class Subject {

	/**
	 * 用来保存注册的观察者对象
	 */
	private List<Observer> observers = new ArrayList<Observer>();
	
	/**
	 * 注册观察者对象
	 * @author mp
	 * @date 2013-9-7 下午3:43:08
	 * @param observer
	 * @Description
	 */
	public void attach(Observer observer){
		observers.add(observer);
	}
	
	/**
	 * 删除观察者对象
	 * @author mp
	 * @date 2013-9-7 下午3:42:57
	 * @param observer
	 * @Description
	 */
	public void detach(Observer observer){
		observers.remove(observer);
	}
	
	/**
	 * 通知所有注册的观察者对象
	 * @author mp
	 * @date 2013-9-7 下午3:43:45
	 * @Description
	 */
	protected void notifyObervers(){
		for(Observer observer : observers){
			observer.update(this);
		}
	}
}
