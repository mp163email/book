package com.mp.design.abstractfactory.sample;

/**
 * 抽象工厂方法，声明创建抽象产品对象的操作
 * @author mp
 * @date 2013-9-5 下午4:31:07
 */
public interface AbstractFactory {
	
	/**
	 * 创建CPU的对象
	 * @author mp
	 * @date 2013-9-5 下午4:31:42
	 * @return
	 * @Description
	 */
	public CPUApi createCpuApi();
	
	/**
	 * 创建主板的对象
	 * @author mp
	 * @date 2013-9-5 下午4:32:10
	 * @return
	 * @Description
	 */
	public MainboardApi createMainboardApi();
	
}
