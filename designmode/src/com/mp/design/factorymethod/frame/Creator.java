package com.mp.design.factorymethod.frame;

/**
 * 创建器  ， 声明工厂方法
 * @author mp
 * @date 2013-9-5 下午2:34:54
 */
public abstract class Creator {

	/**
	 * 创建Product的工厂方法
	 * @author mp
	 * @date 2013-9-5 下午2:36:08
	 * @return
	 * @Description
	 */
	protected abstract Product factoryMethod();
	
	/**
	 * 实现某些功能的方法
	 * @author mp
	 * @date 2013-9-5 下午2:37:08
	 * @Description
	 */
	public void someOperation(){
		//通常在这些方法实现中需要调用工厂方法来获取Product对象
		Product product = factoryMethod();
	}
}
