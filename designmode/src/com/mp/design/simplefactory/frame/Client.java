package com.mp.design.simplefactory.frame;

public class Client {
	
	public static void main(String[] args) {
		
		Api api = Factory.createApi(1);
		api.operate("hello");
		
		api = Factory.createApi(2);
		api.operate("hello");
	}
	
}
