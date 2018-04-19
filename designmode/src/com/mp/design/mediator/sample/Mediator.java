package com.mp.design.mediator.sample;

/**
 * 中介者对象的接口
 * @author mp
 * @date 2013-9-7 上午8:59:09
 */
public interface Mediator {

	/**
	 * 同事对象在自身改变的时候来通知中介者的方法，让中介者去负责相应的与其他同事对象的交互
	 * @author mp
	 * @date 2013-9-7 上午8:39:33
	 * @param colleague 同事对象自身，好让中介者对象通过对象实例去获取同事对象状态
	 * @Description
	 */
	public void changed(Colleague colleague);
}
