package com.mp.design.factorymethod.sample;

/**
 * 导出成文本文件格式的对象
 * @author mp
 * @date 2013-9-5 下午3:06:03
 */
public class ExportTxtFile implements ExportFileApi{

	@Override
	public boolean export(String data) {
		//简单示意一下，这里需要操作文件
		System.out.println("导出数据"+data+"到文本文件");
		return true;
	}

}
