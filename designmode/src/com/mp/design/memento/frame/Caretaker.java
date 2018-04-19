package com.mp.design.memento.frame;

/**
 * 负责保存备忘录的对象
 * @author mp
 * @date 2013-9-8 下午12:44:04
 */
public class Caretaker {

	/**
	 * 记录被保存的备忘录对象
	 */
	private Memento memento = null;
	
	/**
	 * 保存备忘录对象
	 * @author mp
	 * @date 2013-9-8 下午12:46:28
	 * @param memento
	 * @Description
	 */
	public void saveMemento(Memento memento){
		this.memento = memento;
	}
	
	/**
	 * 获取被保存的备忘录对象
	 * @author mp
	 * @date 2013-9-8 下午12:46:42
	 * @return
	 * @Description
	 */
	public Memento retriveMemento(){
		return this.memento;
	}
	
	
}
