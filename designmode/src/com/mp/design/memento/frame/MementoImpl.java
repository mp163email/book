package com.mp.design.memento.frame;

/**
 * 真正的备忘录对象，实现备忘录窄接口
 * @author mp
 * @date 2013-9-8 下午12:40:38
 */
public class MementoImpl implements Memento{

	private String state = "";
	
	public MementoImpl(String state){
		this.state = state;
	}

	public String getState() {
		return state;
	}
	
}
