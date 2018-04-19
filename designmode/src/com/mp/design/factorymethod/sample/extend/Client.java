package com.mp.design.factorymethod.sample.extend;

/**
 * 导出成数据库格式或者文件格式的文件  示例
 * @author mp
 * @date 2013-9-5 下午3:18:15
 */
public class Client {

	public static void main(String[] args) {
		ExportOperate exportOperate = new ExportOperate2();
		exportOperate.export(1, "测试数据");
		exportOperate.export(2, "测试数据");
		exportOperate.export(3, "测试数据");
	}
}
