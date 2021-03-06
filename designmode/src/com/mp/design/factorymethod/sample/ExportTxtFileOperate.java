package com.mp.design.factorymethod.sample;

/**
 * 具体的创建器实现对象，实现创建导出成文本文件格式的对象
 * @author mp
 * @date 2013-9-5 下午3:12:16
 */
public class ExportTxtFileOperate extends ExportOperate{

	@Override
	protected ExportFileApi factoryMethod() {
		//创建导出成文本文件格式的对象
		return new ExportTxtFile();
	}
	
}
