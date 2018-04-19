package com.mp.design.mediator.sample;

/**
 * 声卡类， 一个同事类
 * 
 * @author mp
 * @date 2013-9-7 上午9:17:41
 */
public class SoundCard extends Colleague {

	public SoundCard(Mediator mediator) {
		super(mediator);
	}

	/**
	 * 发出声音
	 * @author mp
	 * @date 2013-9-7 上午9:22:11
	 * @param data
	 * @Description
	 */
	public void soundData(String data) {
		System.out.println("画外音：" + data);
	}
}
