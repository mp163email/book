package com.mp.design.composite.frame;

/**
 * 叶子对象，叶子对象不再包含其他子对象
 * @author mp
 * @date 2013-9-8 上午11:24:05
 */
public class Leaf extends Component{

	/**
	 * 示意方法，叶子对象对象可能有自己的功能方法
	 */
	@Override
	public void someOperation() {
		// do something
	}

}
