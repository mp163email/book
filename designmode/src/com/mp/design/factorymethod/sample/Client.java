package com.mp.design.factorymethod.sample;

/**
 * 导出成数据库格式或者文件格式的文件  示例
 * @author mp
 * @date 2013-9-5 下午3:18:15
 */
public class Client {

	public static void main(String[] args) {
		//创建需要使用的Creator对象
		ExportOperate operate = new ExportDBOperate();
		//调用输出数据的功能方法
		operate.export("测试数据");
		
		operate = new ExportTxtFileOperate();
		operate.export("测试数据");
	}
}
