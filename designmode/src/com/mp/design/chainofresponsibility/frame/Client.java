package com.mp.design.chainofresponsibility.frame;

/**
 * 职责连的客户端，只是个示意
 * @author mp
 * @date 2013-9-8 下午1:37:42
 */
public class Client {

	public static void main(String[] args) {
		//先要组装职责连
		Handler h1 = new ConcreteHandler1();
		Handler h2 = new ConcreteHandler2();
		
		h1.setSuccessor(h2);
		h1.handleRequest();
	}
}
