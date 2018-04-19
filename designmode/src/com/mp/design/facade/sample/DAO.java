package com.mp.design.facade.sample;

/**
 * 示意生成数据层模块
 * @author mp
 * @date 2013-9-4 下午2:53:06
 */
public class DAO {
	
	public void generate(){
		ConfigModel cm = ConfigManager.getInstance().getConfigData();
		if(cm.isNeedGenDAO()){
			System.out.println("正在生成数据访问层代码文件");
		}
	}
}
