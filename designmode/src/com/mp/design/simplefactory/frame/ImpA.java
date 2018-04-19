package com.mp.design.simplefactory.frame;

public class ImpA implements Api {

	@Override
	public void operate(String s) {
		System.out.println("ImpA s == " + s);
	}

}
