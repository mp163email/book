package com.mp.design.command.sample;


/**
 * 技嘉主板类，开机命令的真正实现者，在Commond模式中充当Receiver
 * @author mp
 * @date 2013-9-7 下午4:39:13
 */
public class GigaMainBoard implements MainBoardApi{

	@Override
	public void open() {
		System.out.println("技嘉主板正开机");
		System.out.println("接通电源");
		System.out.println("检查设备");
		System.out.println("装载系统");
		System.out.println("正常运转，正常打开，请操作");
	}
}
