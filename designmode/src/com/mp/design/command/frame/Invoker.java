package com.mp.design.command.frame;

/**
 * 调用者
 * @author mp
 * @date 2013-9-7 下午4:32:45
 */
public class Invoker {
	
	/**
	 * 持有命令对象
	 */
	private Command command = null;
	
	public void setCommand(Command command){
		this.command = command;
	}
	
	public void runCommand(){
		//调用命令对象的执行方法
		command.execute();
	}
}
