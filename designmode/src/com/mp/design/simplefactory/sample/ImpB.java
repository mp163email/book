package com.mp.design.simplefactory.sample;

public class ImpB implements Api{

	@Override
	public void operate(String s) {
		System.out.println("ImpB s == " + s);
	}
}
