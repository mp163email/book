package com.mp.design.abstractfactory.sample;

/**
 * Intel的CPU实现
 * 
 * @author mp
 * @date 2013-9-5 下午4:23:10
 */
public class IntelCPU implements CPUApi {

	/**
	 * CPU的针脚数目
	 */
	private int pins = 0;

	public IntelCPU(int pins) {
		this.pins = pins;
	}

	@Override
	public void calculate() {
		System.out.println("now in Intel CPU, pins=" + pins);
	}

}
