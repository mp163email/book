package com.mp.design.factorymethod.sample;

/**
 * 导出成数据库备份文件格式的对象
 * @author mp
 * @date 2013-9-5 下午3:06:03
 */
public class ExportDB implements ExportFileApi{

	@Override
	public boolean export(String data) {
		//简单示意一下，这里需要操作数据库和文件
		System.out.println("导出数据"+data+"到数据库备份文件");
		return true;
	}

}
