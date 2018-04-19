package com.mp.design.mediator.sample;

/**
 * 显卡类，一个同事类
 * 
 * @author mp
 * @date 2013-9-7 上午9:14:37
 */
public class VideoCard extends Colleague {

	public VideoCard(Mediator mediator) {
		super(mediator);
	}

	/**
	 * 显示视频数据
	 * @author mp
	 * @date 2013-9-7 上午9:16:06
	 * @param data
	 * @Description
	 */
	public void showData(String data) {
		System.out.println("您正观看的是：" + data);
	}
}
