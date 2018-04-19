package com.mp.design.prototype.sample;

/**
 * 企业订单对象
 * 
 * @author mp
 * @date 2013-9-6 上午11:37:00
 */
public class EnterpriseOrder implements OrderApi {

	/**
	 * 企业姓名
	 */
	private String enterpriseName;

	/**
	 * 产品编号
	 */
	private String productId;

	/**
	 * 订单产品数量
	 */
	private int orderProductNum = 0;

	public String getEnterpriseName() {
		return enterpriseName;
	}

	public void setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public int getOrderProductNum() {
		return orderProductNum;
	}

	public void setOrderProductNum(int orderProductNum) {
		this.orderProductNum = orderProductNum;
	}

	@Override
	public OrderApi cloneOrder() {
		// 创建一个新的订单，然后把本实例的数据复制过去
		EnterpriseOrder order = new EnterpriseOrder();
		order.setEnterpriseName(this.enterpriseName);
		order.setProductId(this.productId);
		order.setOrderProductNum(this.orderProductNum);
		return order;
	}

	public String toString() {
		return "本企业订单的订购企业是" + this.enterpriseName + ", 订购产品是:"
				+ this.productId + ", 订购数量为：" + this.orderProductNum;
	}

}
