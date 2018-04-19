package com.mp.design.command.frame;

/**
 * 示意，负责创建命令对象，并设定他的接收者
 * @author mp
 * @date 2013-9-7 下午4:35:13
 */
public class Client {

	public void assemble(){
		//创建接受者
		Receiver receiver = new Receiver();
		//创建命令对象
		Command command = new ConcreteCommand(receiver);
		//创建Invoker,把命令对象设置进去
		Invoker invoker = new Invoker();
		invoker.setCommand(command);
	}
}
