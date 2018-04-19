package com.mp.design.factorymethod.sample.extend;

import com.mp.design.factorymethod.sample.ExportDB;
import com.mp.design.factorymethod.sample.ExportFileApi;
import com.mp.design.factorymethod.sample.ExportTxtFile;

/**
 * 实现导出数据的业务功能对象
 * @author mp
 * @date 2013-9-5 下午3:09:03
 */
public class ExportOperate {

	/**
	 * 工厂方法，创建导出的文件对象的接口对象
	 * @author mp
	 * @date 2013-9-5 下午3:10:06
	 * @return
	 * @Description
	 */
	protected ExportFileApi factoryMethod(int type){
		ExportFileApi api = null;
		if(type == 1){
			api = new ExportTxtFile();
		}else if (type == 2){
			api = new ExportDB();
		}
		return api;
	}
	
	/**
	 * 导出文件
	 * @author mp
	 * @date 2013-9-5 下午3:11:00
	 * @param data
	 * @return
	 * @Description
	 */
	public boolean export(int type, String data){
		
		//使用工厂方法
		ExportFileApi api = factoryMethod(type);
		return api.export(data);
	}
}
