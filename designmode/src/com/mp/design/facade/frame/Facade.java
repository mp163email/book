package com.mp.design.facade.frame;

public class Facade {
	
	/**
	 * 示意方法，满足客户需要的功能
	 * @author mp
	 * @date 2013-9-4 下午12:18:43
	 * @Description
	 */
	public void test(){
		
		//在内部实现的时候，可能会调用到内部的多个模块
		AModuleApi aModuleApi = new AModuleImpl();
		aModuleApi.testA();
		
		BModuleApi bModuleApi = new BModuleImpl();
		bModuleApi.testB();
		
		CModuleApi cModuleApi = new CModuleImpl();
		cModuleApi.testC();
	}
}
