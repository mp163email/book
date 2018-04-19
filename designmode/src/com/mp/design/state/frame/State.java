package com.mp.design.state.frame;

/**
 * 封装与Context的一个特定状态相关行为
 * @author mp
 * @date 2013-9-8 下午12:19:11
 */
public interface State {

	/**
	 * 状态对应的处理
	 * @author mp
	 * @date 2013-9-8 下午12:20:05
	 * @param sampleParameter
	 * @Description
	 */
	public void handle(String sampleParameter);
}
