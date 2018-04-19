package com.mp.design.command.frame;

/**
 * 具体的命令实现对象
 * @author mp
 * @date 2013-9-7 下午4:26:59
 */
public class ConcreteCommand implements Command{

	/**
	 * 持有相应的接收者对象
	 */
	private Receiver receiver = null;
	
	/**
	 * 示意：命令对象可以有自己的状态
	 */
	private String state;
	
	public ConcreteCommand(Receiver receiver){
		this.receiver = receiver;
	}
	
	@Override
	public void execute() {
		//通常会转掉接受者对象的相应方法，让接受者来真正执行功能
		receiver.action();
	}

}
