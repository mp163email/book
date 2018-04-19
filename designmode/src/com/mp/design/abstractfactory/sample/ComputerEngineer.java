package com.mp.design.abstractfactory.sample;

/**
 * 装机工程师类
 * @author mp
 * @date 2013-9-5 下午4:37:34
 */
public class ComputerEngineer {

	/**
	 * 定义组装电脑需要的CPU
	 */
	private CPUApi cpu = null;
	
	/**
	 * 定义组装电脑需要的主板
	 */
	private MainboardApi mainboard = null;
	
	/**
	 * 装机过程
	 * @author mp
	 * @date 2013-9-5 下午4:39:05
	 * @param schema
	 * @Description
	 */
	public void makeComputer(AbstractFactory schema){
		//1.首先准备好装机所需要的配件
		prepareHardwares(schema);
		//2.组装电脑
		//3.测试电脑
		//4.交付客户
	}
	
	/**
	 * 准备装机所需要的配件
	 * @author mp
	 * @date 2013-9-5 下午4:40:57
	 * @param schema
	 * @Description
	 */
	private void prepareHardwares(AbstractFactory schema){
		
		//这里要去准备CPU和主板的具体实现，为了示例简单，这里只准备两个
		//可是，装机工程师并不知道如何去创建怎么办？
		
		//使用抽象工厂来获取相应的接口对象，面向接口编程
		this.cpu = schema.createCpuApi();
		this.mainboard = schema.createMainboardApi();
		
		//测试一下配件是否好用
		this.cpu.calculate();
		this.mainboard.installCPU();
	}
	
	
}
