package com.mp.design.bridge.frame;

/**
 * 扩充由Abstraction定义的接口功能
 * @author mp
 * @date 2013-9-8 下午1:46:20
 */
public class RefineAbstraction extends Abstraction{

	public RefineAbstraction(Implementor impl) {
		super(impl);
	}

	/**
	 * 示例操作，实现一定的功能 
	 * @author mp
	 * @date 2013-9-8 下午1:47:12
	 * @Description
	 */
	public void otherOperation(){
		//实现一定的功能，可能会使用具体实现部分的实现方法
		//但是本方法更大的可能是使用Abstraction中定义的方法
		//通过组合使用Abstraction中定义的方法来完成更多的功能
	}
}
