package com.mp.design.facade.sample;

/**
 * 示意生成业务逻辑层模块
 * @author mp
 * @date 2013-9-4 下午2:53:06
 */
public class Business {
	
	public void generate(){
		ConfigModel cm = ConfigManager.getInstance().getConfigData();
		if(cm.isNeedGenBusiness()){
			System.out.println("正在生成业务逻辑层代码文件");
		}
	}
}
