package com.mp.design.decorator.frame;

/**
 * 装饰器的具体实现对象，向组件对象添加职责
 * @author mp
 * @date 2013-9-8 下午1:26:33
 */
public class ConcreteDecoratorB extends Decorator{

	public ConcreteDecoratorB(Component component) {
		super(component);
	}

	private void addedBehavior(){
		//需要添加的职责实现
	}
	
	public void operation(){
		//调用父类的方法，可以在调用前后执行一些附加的动作
		super.operation();
		addedBehavior();
	}
	
}
