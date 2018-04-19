package com.mp.design.factorymethod.sample.extend;

import com.mp.design.factorymethod.sample.ExportFileApi;

/**
 * 导出成xml文件的对象
 * @author mp
 * @date 2013-9-5 下午3:39:30
 */
public class ExportXml implements ExportFileApi{

	@Override
	public boolean export(String data) {
		
		//简单示意一下
		System.out.println("导出数据"+data+"到xml文件");
		return true;
	}
	
}
