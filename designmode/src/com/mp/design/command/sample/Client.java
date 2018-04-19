package com.mp.design.command.sample;

/**
 * 计算机开机 实例
 * @author mp
 * @date 2013-9-7 下午4:49:01
 */
public class Client {

	public static void main(String[] args) {
		
		//把命令和真正的实现组合起来，相当于组装机器
		//把机箱上按钮的连接线插到主板上
		MainBoardApi mainBoard = new GigaMainBoard();
		OpenCommand openCommand = new OpenCommand(mainBoard);
		
		//为机箱上的按钮设置对应的命令，让按钮知道该干什么
		Box box = new Box();
		box.setOpenCommand(openCommand);
		
		//模拟按下机箱上的开机按钮
		box.openButtonPressed();
	}
	
}
