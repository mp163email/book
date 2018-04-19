package com.mp.design.mediator.sample;

/**
 * CPU类，一个同事类
 * @author mp
 * @date 2013-9-7 上午9:09:17
 */
public class CPU extends Colleague{

	public CPU(Mediator mediator) {
		super(mediator);
	}
	
	/**
	 * 分解出来的视频数据
	 */
	private String videoData = "";
	
	/**
	 * 分解出来的声音数据
	 */
	private String soundData = "";
	
	/**
	 * 获取分解出来的视频数据
	 * @author mp
	 * @date 2013-9-7 上午9:11:17
	 * @return
	 * @Description
	 */
	public String getVideoData() {
		return videoData;
	}

	/**
	 * 获取分解出来的声音数据
	 * @author mp
	 * @date 2013-9-7 上午9:11:17
	 * @return
	 * @Description
	 */
	public String getSoundData() {
		return soundData;
	}
	
	public void executeData(String data){
		//把数据分开，前面的视频数据，后面的是音频数据
		String [] ss = data.split(",");
		this.videoData = ss[0];
		this.soundData = ss[1];
		//通知主板，CPU的工作完成
		this.getMediator().changed(this);
	}
	
}
