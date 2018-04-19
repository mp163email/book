package com.mp.design.command.sample;

/**
 * 开机命令的实现，实现Command接口
 * 持有开机命令的真正实现，通过调用接收者的方法来实现命令
 * @author mp
 * @date 2013-9-7 下午4:42:34
 */
public class OpenCommand implements Command{

	private MainBoardApi mainBoard = null;
	
	public OpenCommand(MainBoardApi mainBoard){
		this.mainBoard = mainBoard;
	}

	@Override
	public void execute() {
		//对于命令对象，根本不知道如何开机，会转掉主板对象
		//让主板去完成开机功能
		this.mainBoard.open();
	}
	
}
