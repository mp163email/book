package com.mp.design.builder.sample;

import java.util.Collection;
import java.util.Map;

/**
 * 生成器接口，定义创建一个输出文件对象所需要的各个部件的操作
 * @author mp
 * @date 2013-9-6 上午10:02:19
 */
public interface Builder {

	/**
	 * 构建输出文件的Header部分
	 * @author mp
	 * @date 2013-9-6 上午10:04:05
	 * @param ehm
	 * @Description
	 */
	public void buildHeader(ExportHeaderModel ehm);
	
	/**
	 * 构建输出文件的Body部分
	 * @author mp
	 * @date 2013-9-6 上午10:04:26
	 * @param mapData
	 * @Description
	 */
	public void buildBody(Map<String, Collection<ExportDataModel>> mapData);
	
	/**
	 * 构建输出文件的Footer部分
	 * @author mp
	 * @date 2013-9-6 上午10:04:39
	 * @param efm
	 * @Description
	 */
	public void buildFooter(ExportFooterModel efm);
}
