package com.mp.design.abstractfactory.sample;

/**
 * 技嘉的主板
 * 
 * @author mp
 * @date 2013-9-5 下午4:26:47
 */
public class GAMainboard implements MainboardApi {

	/**
	 * CPU插槽的孔数
	 */
	private int cpuHoles = 0;

	public GAMainboard(int cpuHoles) {
		this.cpuHoles = cpuHoles;
	}

	@Override
	public void installCPU() {
		System.out.println("now in GAMainboard, cpuHoles=" + cpuHoles);
	}

}
