package com.mp.design.memento.frame;

/**
 * 原发器对象
 * @author mp
 * @date 2013-9-8 下午12:34:05
 */
public class Originator {
	
	/**
	 * 示意，表示原发器的状态
	 */
	private String state = "";
	
	/**
	 * 创建保存原发器对象的状态的备忘录对象
	 * @author mp
	 * @date 2013-9-8 下午12:37:48
	 * @return
	 * @Description
	 */
	public Memento createMemento(){
		return new MementoImpl(state);
	}
	
	/**
	 * 重新设置原发器对象的状态，让其回到备忘录对象记录的状态
	 * @author mp
	 * @date 2013-9-8 下午12:38:38
	 * @param memento
	 * @Description
	 */
	public void setMemento(Memento memento){
		MementoImpl mementoImpl = (MementoImpl)memento;
		this.state = mementoImpl.getState();
	}
	
}
