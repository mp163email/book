package com.mp.design.adapter.frame;

/**
 * 适配器
 * @author mp
 * @date 2013-9-4 下午3:32:33
 */
public class Adapter implements Target{

	/**
	 * 持有需要被适配的接口对象
	 */
	private Adaptee adaptee;
	
	public Adapter(Adaptee adaptee){
		this.adaptee = adaptee;
	}
	
	@Override
	public void request() {
		//转掉已经实现了的方法，进行适配
		adaptee.specificRequest();
	}

}
