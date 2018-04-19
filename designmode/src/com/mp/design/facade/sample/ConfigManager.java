package com.mp.design.facade.sample;

/**
 * 示意配置管理， 负责读取配置文件
 * 并把配置文件的内容该设置到配置Model中，是个单例
 * @author mp
 * @date 2013-9-4 下午2:48:38
 */
public class ConfigManager {
	
	private static ConfigManager manager = null;
	private static ConfigModel cm = null;
	
	private ConfigManager(){
		
	}
	
	public static ConfigManager getInstance(){
		if(manager == null){
			manager = new ConfigManager();
			cm = new ConfigModel();
			//读取配置文件，把值设置到ConfigModel中，此处省略
		}
		return manager;
	}
	
	/**
	 * 获取配置的数据
	 * @author mp
	 * @date 2013-9-4 下午2:52:05
	 * @return
	 * @Description
	 */
	public ConfigModel getConfigData(){
		return cm;
	}
}
