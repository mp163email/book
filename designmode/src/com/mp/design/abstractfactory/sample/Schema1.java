package com.mp.design.abstractfactory.sample;

/**
 * 装机方案一：Intel 的CPU +　技嘉的主板
 * 这里创建CPU和主板对象的时候，是对应的，能匹配上的
 * @author mp
 * @date 2013-9-5 下午4:33:21
 */
public class Schema1 implements AbstractFactory{

	@Override
	public CPUApi createCpuApi() {
		return new IntelCPU(1156);
	}

	@Override
	public MainboardApi createMainboardApi() {
		return new GAMainboard(1156);
	}

}
