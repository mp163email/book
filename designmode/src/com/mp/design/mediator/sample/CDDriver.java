package com.mp.design.mediator.sample;

/**
 * 光驱类，一个同事类
 * @author mp
 * @date 2013-9-7 上午9:05:13
 */
public class CDDriver extends Colleague{

	public CDDriver(Mediator mediator) {
		super(mediator);
	}

	/**
	 * 光驱读出来的数据
	 */
	private String data = "";
	
	/**
	 * 获取光驱读出来的数据
	 * @author mp
	 * @date 2013-9-7 上午9:06:51
	 * @return
	 * @Description
	 */
	public String getData(){
		return this.data;
	}
	
	/**
	 * 读取光盘
	 * @author mp
	 * @date 2013-9-7 上午9:07:11
	 * @Description
	 */
	public void readCD(){
		//逗号前的是视频显示的数据，逗号后的是声音
		this.data = "设计模式,值得好好研究";
		this.getMediator().changed(this);
	}

}
