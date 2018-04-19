package com.mp.design.abstractfactory.sample;

/**
 * 装机方案二： AMD的CPU + 微星的主板
 * 这里创建CPU和主板对象的时候，是对应的，能匹配上的
 * @author mp
 * @date 2013-9-5 下午4:35:44
 */
public class Schema2 implements AbstractFactory{

	@Override
	public CPUApi createCpuApi() {
		return new AMDCPU(939);
	}

	@Override
	public MainboardApi createMainboardApi() {
		return new MSIMainboard(939);
	}
	
}
