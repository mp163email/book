package com.mp.design.builder.sample;

import java.util.Collection;
import java.util.Map;

/**
 * 实现导出数据到xml文件的生成器对象
 * @author mp
 * @date 2013-9-6 上午10:15:30
 */
public class XmlBuilder implements Builder{
	
	/**
	 * 用来记录构建的文件的内容，相当于产品
	 */
	private StringBuffer buffer = new StringBuffer();

	@Override
	public void buildHeader(ExportHeaderModel ehm) {
		buffer.append("XML ------->"+ehm.getDepId()+","+ehm.getExportDate()+"\n");
	}

	@Override
	public void buildBody(Map<String, Collection<ExportDataModel>> mapData) {
		for(String tblName : mapData.keySet()){
			//先拼接表名称
			buffer.append("XML ------->"+tblName+"\n");
			//循环拼接具体数据
			for(ExportDataModel edm : mapData.get(tblName)){
				buffer.append("XML ------->"+edm.getProductId()+","+edm.getPrice()+","+edm.getAmount()+"\n");
			}
		}
	}

	@Override
	public void buildFooter(ExportFooterModel efm) {
		buffer.append("XML ------->"+efm.getExportUser());
	}

	/**
	 * 获取结果 
	 * @author mp
	 * @date 2013-9-6 上午10:14:40
	 * @return
	 * @Description
	 */
	public StringBuffer getResult(){
		return buffer;
	}
	
}
