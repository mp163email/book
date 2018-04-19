package com.mp.design.abstractfactory.frame;

/**
 * 抽象工厂的接口，声明创建抽象产品对象的操作
 * @author mp
 * @date 2013-9-5 下午4:07:26
 */
public interface AbstracFactory {

	/**
	 * 创建抽象产品A的对象
	 * @author mp
	 * @date 2013-9-5 下午4:08:32
	 * @return
	 * @Description
	 */
	public AbstractProductA createProductA();
	
	/**
	 * 创建抽象产品B的对象
	 * @author mp
	 * @date 2013-9-5 下午4:08:56
	 * @return
	 * @Description
	 */
	public AbstractProductB createProductB();
}
