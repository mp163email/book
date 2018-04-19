package com.mp.design.iterator.sample.design;

/**
 * 工资描述模型对象
 * @author mp
 * @date 2013-9-7 下午5:24:36
 */
public class PayModel {
	
	/**
	 * 支付工资的人员
	 */
	private String userName;
	
	/**
	 * 支付的工资数据
	 */
	private double pay;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public double getPay() {
		return pay;
	}

	public void setPay(double pay) {
		this.pay = pay;
	}
	
}
