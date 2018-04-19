package com.mp.design.facade.sample;

/**
 * 代码生成子系统的外观对象
 * @author mp
 * @date 2013-9-4 下午2:59:57
 */
public class Facade {
	
	/**
	 * 客户端需要的，一个简单的调用代码生成的功能
	 * @author mp
	 * @date 2013-9-4 下午3:01:07
	 * @Description
	 */
	public void generate(){
		new Presentation().generate();
		new Business().generate();
		new DAO().generate();
	}
}
