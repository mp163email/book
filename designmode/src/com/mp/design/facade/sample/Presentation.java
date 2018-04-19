package com.mp.design.facade.sample;

/**
 * 示意生成表现层模块
 * @author mp
 * @date 2013-9-4 下午2:53:06
 */
public class Presentation {
	
	public void generate(){
		//1.从配置管理里面获取相应的配置信息
		ConfigModel cm = ConfigManager.getInstance().getConfigData();
		if(cm.isNeedGenPresentation()){
			//2.按要求生成相应代码，并保存成文件
			System.out.println("正在生成表示层代码文件");
		}
	}
}
