package com.mp.design.factorymethod.sample.extend;

import com.mp.design.factorymethod.sample.ExportFileApi;

/**
 * 扩展ExportOperate对象，加入可以导出xml文件
 * @author mp
 * @date 2013-9-5 下午3:41:37
 */
public class ExportOperate2 extends ExportOperate{

	/**
	 * 覆盖父类的工厂方法，创建导出的文件对象的接口对象
	 */
	protected ExportFileApi factoryMethod(int type){
		ExportFileApi api = null;
		//可以全部覆盖，也可以选择自己感兴趣的覆盖
		//这里只想添加自己新的实现，其他的不管
		if(type == 3){
			api = new ExportXml();
		}else{
			api = super.factoryMethod(type);
		}
		return api;
	}
}
