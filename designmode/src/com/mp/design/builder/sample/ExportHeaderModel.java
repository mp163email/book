package com.mp.design.builder.sample;

/**
 * 描述输出到文件头的内容的对象
 * @author mp
 * @date 2013-9-6 上午9:56:05
 */
public class ExportHeaderModel {

	/**
	 * 分公司或市点编号
	 */
	private String depId;
	
	/**
	 * 导出数据的日期
	 */
	private String exportDate;
	

	public String getDepId() {
		return depId;
	}

	public void setDepId(String depId) {
		this.depId = depId;
	}

	public String getExportDate() {
		return exportDate;
	}

	public void setExportDate(String exportDate) {
		this.exportDate = exportDate;
	}
	
	
	
}
