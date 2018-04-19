package com.mp.design.command.sample;

/**
 * 机箱对象，本身有按钮，持有按钮对应的命令对象
 * @author mp
 * @date 2013-9-7 下午4:45:29
 */
public class Box {

	/**
	 * 开机命令对象
	 */
	private Command openCommand;
	
	/**
	 * 设置开机命令对象
	 * @author mp
	 * @date 2013-9-7 下午4:46:37
	 * @param command
	 * @Description
	 */
	public void setOpenCommand(Command command){
		this.openCommand = command;
	}
	
	/**
	 * 提供给客户使用，接收并相应用户请求，相当于按钮被按下触发的方法
	 * @author mp
	 * @date 2013-9-7 下午4:46:59
	 * @Description
	 */
	public void openButtonPressed(){
		//按下按钮，执行命令
		openCommand.execute();
	}
	
}
