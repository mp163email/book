package com.mp.design.factorymethod.sample;

/**
 * 实现导出数据的业务功能对象
 * @author mp
 * @date 2013-9-5 下午3:09:03
 */
public abstract class ExportOperate {

	/**
	 * 工厂方法，创建导出的文件对象的接口对象
	 * @author mp
	 * @date 2013-9-5 下午3:10:06
	 * @return
	 * @Description
	 */
	protected abstract ExportFileApi factoryMethod();
	
	/**
	 * 导出文件
	 * @author mp
	 * @date 2013-9-5 下午3:11:00
	 * @param data
	 * @return
	 * @Description
	 */
	public boolean export(String data){
		
		//使用工厂方法
		ExportFileApi api = factoryMethod();
		return api.export(data);
	}
}
