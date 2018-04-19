package com.mp.design.simplefactory.sample;

public class ImpA implements Api {

	@Override
	public void operate(String s) {
		System.out.println("ImpA s == " + s);
	}

}
