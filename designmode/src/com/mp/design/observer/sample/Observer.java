package com.mp.design.observer.sample;

/**
 * 观察者，比如报纸的读者
 * @author mp
 * @date 2013-9-7 下午3:53:06
 */
public interface Observer {
	
	/**
	 * 被通知的方法
	 * @author mp
	 * @date 2013-9-7 下午3:53:40
	 * @param subject
	 * @Description
	 */
	public void update(Subject subject);
}
