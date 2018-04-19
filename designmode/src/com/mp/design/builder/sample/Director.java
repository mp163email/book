package com.mp.design.builder.sample;

import java.util.Collection;
import java.util.Map;

/**
 * 指导者，指导使用生成器接口来构建输出的文件对象
 * @author mp
 * @date 2013-9-6 上午10:18:01
 */
public class Director {

	/**
	 * 持有当前需要使用的生成器对象
	 */
	private Builder builder;
	
	/**
	 * 构造方法,传入生成器对象
	 * @param builder
	 */
	public Director(Builder builder){
		this.builder = builder;
	}
	
	/**
	 * 指导生成器构建最终的输出的文件的对象
	 * @author mp
	 * @date 2013-9-6 上午10:21:05
	 * @param ehm
	 * @param mapData
	 * @param efm
	 * @Description
	 */
	public void construct(ExportHeaderModel ehm, Map<String, Collection<ExportDataModel>> mapData, ExportFooterModel efm){
		
		//1.构建Header
		builder.buildHeader(ehm);
		
		//2.然后构建Body
		builder.buildBody(mapData);
		
		//3.再构建Footer
		builder.buildFooter(efm);
		
	}
	
	
}
