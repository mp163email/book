package com.mp.design.visitor.frame;

/**
 * 具体的访问者实现
 * @author mp
 * @date 2013-9-8 下午1:56:16
 */
public class ConcreetVisitor1 implements Visitor{

	@Override
	public void visitConcreteElementA(ConcreteElementA element) {
		
		//要把访问ConcreteElementA时，需要执行的功能实现在这里
		//可能需要访问元素已有的功能
		element.operationA();
	}

	@Override
	public void visitConcreteElementB(ConcreteElementB element) {
		//要把访问ConcreteElementA时，需要执行的功能实现在这里
		//可能需要访问元素已有的功能
		element.operationB();
	}
	
}
