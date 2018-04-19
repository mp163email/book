package com.mp.design.prototype.sample;

/**
 * 个人订单对象
 * 
 * @author mp
 * @date 2013-9-6 上午11:37:00
 */
public class PersonalOrder implements OrderApi {

	/**
	 * 订购人员姓名
	 */
	private String customerName;

	/**
	 * 产品编号
	 */
	private String productId;

	/**
	 * 订单产品数量
	 */
	private int orderProductNum = 0;

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
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
		PersonalOrder order = new PersonalOrder();
		order.setCustomerName(this.customerName);
		order.setProductId(this.productId);
		order.setOrderProductNum(this.orderProductNum);
		return order;
	}

	public String toString() {
		return "本个人订单的订购人员是" + this.customerName + ", 订购产品是:" + this.productId
				+ ", 订购数量为：" + this.orderProductNum;
	}

}
