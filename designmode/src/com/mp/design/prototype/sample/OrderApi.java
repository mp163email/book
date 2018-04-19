package com.mp.design.prototype.sample;

/**
 * 订单的接口
 * 
 * @author mp
 * @date 2013-9-6 上午11:34:59
 */
public interface OrderApi {

	/**
	 * 获取订单产品数量
	 * 
	 * @author mp
	 * @date 2013-9-6 上午11:35:31
	 * @return
	 * @Description
	 */
	public int getOrderProductNum();

	/**
	 * 设置订单产品数量
	 * 
	 * @author mp
	 * @date 2013-9-6 上午11:35:41
	 * @param num
	 * @Description
	 */
	public void setOrderProductNum(int num);

	/**
	 * 克隆方法
	 * 
	 * @author mp
	 * @date 2013-9-6 上午11:45:52
	 * @return
	 * @Description
	 */
	public OrderApi cloneOrder();
}
