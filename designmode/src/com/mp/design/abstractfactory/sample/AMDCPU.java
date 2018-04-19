package com.mp.design.abstractfactory.sample;

/**
 * AMD的CPU实现
 * 
 * @author mp
 * @date 2013-9-5 下午4:23:10
 */
public class AMDCPU implements CPUApi {

	/**
	 * CPU的针脚数目
	 */
	private int pins = 0;

	public AMDCPU(int pins) {
		this.pins = pins;
	}

	@Override
	public void calculate() {
		System.out.println("now in AMD CPU, pins=" + pins);
	}

}
