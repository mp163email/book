package com.mp.design.abstractfactory.sample;

/**
 * 装配电脑 示例
 * @author mp
 * @date 2013-9-5 下午4:44:58
 */
public class Client {

	public static void main(String[] args) {
		//创建装机工程师对象
		ComputerEngineer engineer = new ComputerEngineer();
		//客户选择并创建需要使用的装机方案对象
		AbstractFactory schema = new Schema2();
		//告诉工程师自己选择的装机方案，让装机工程师组装电脑
		engineer.makeComputer(schema);
	}
}
