package com.mp.design.decorator.frame;

/**
 * 装饰器的具体实现对象，向组件对象添加职责
 * @author mp
 * @date 2013-9-8 下午1:22:38
 */
public class ConcreteDecoratorA extends Decorator{

	public ConcreteDecoratorA(Component component) {
		super(component);
	}

	/**
	 * 添加状态
	 */
	private String addedState;

	public String getAddedState() {
		return addedState;
	}

	public void setAddedState(String addedState) {
		this.addedState = addedState;
	}
	
	public void operation(){
		//调用父类的方法，可以在调用前后执行一些附加动作
		//在这里进行处理的时候，可以使用添加的状态
		super.operation();
	}
}
