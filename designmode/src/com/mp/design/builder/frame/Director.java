package com.mp.design.builder.frame;

/**
 * 指导者，指导使用生成器的接口来构建产品的对象
 * @author mp
 * @date 2013-9-5 下午6:17:30
 */
public class Director {

	/**
	 * 持有当前需要使用的生成器对象
	 */
	private Builder builder;
	
	public Director(Builder builder){
		this.builder = builder;
	}
	
	/**
	 * 示意方法， 指导生成器构建最终的产品对象
	 * @author mp
	 * @date 2013-9-5 下午6:19:04
	 * @Description
	 */
	public void construct(){
		
		//通过使用生成器接口来构建最终的产品对象
		builder.buildPart();
	}
}
