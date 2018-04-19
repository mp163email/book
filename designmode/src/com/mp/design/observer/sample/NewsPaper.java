package com.mp.design.observer.sample;

/**
 * 报纸对象，具体的目标实现
 * @author mp
 * @date 2013-9-7 下午3:58:10
 */
public class NewsPaper extends Subject{

	/**
	 * 具体内容
	 */
	private String content;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
		notifyObservers();
	}
	
}
