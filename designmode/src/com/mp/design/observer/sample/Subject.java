package com.mp.design.observer.sample;

import java.util.ArrayList;
import java.util.List;

/**
 * 目标对象，作为被观察者
 * @author mp
 * @date 2013-9-7 下午3:52:09
 */
public class Subject {

	/**
	 * 用来保存注册的观察者对象，也就是报纸的订阅者
	 */
	private List<Observer> readers = new ArrayList<Observer>();
	
	/**
	 * 读者借阅需要注册
	 * @author mp
	 * @date 2013-9-7 下午3:55:36
	 * @param reader
	 * @Description
	 */
	public void attach(Observer reader){
		readers.add(reader);
	}
	
	/**
	 * 取消订阅
	 * @author mp
	 * @date 2013-9-7 下午3:56:11
	 * @param reader
	 * @Description
	 */
	public void detach(Observer reader){
		readers.remove(reader);
	}
	
	/**
	 * 当报纸出来后，通知读者
	 * @author mp
	 * @date 2013-9-7 下午3:56:44
	 * @Description
	 */
	protected void notifyObservers(){
		for(Observer reader : readers){
			reader.update(this);
		}
	}
}
