package com.mp.design.builder.frame;


/**
 * 具体的生成器实现对象
 * @author mp
 * @date 2013-9-5 下午6:13:10
 */
public class ConcreteBuilder implements Builder{

	/**
	 * 生成器最终构建的产品对象
	 */
	private Product resultProduct;
	
	/**
	 * 获取生成器最终构建的产品对象 
	 * @author mp
	 * @date 2013-9-5 下午6:14:20
	 * @return
	 * @Description
	 */
	public Product getResult(){
		return resultProduct;
	}

	@Override
	public void buildPart() {
		//构建某个部件的功能处理
	}
}
