package com.mp.design.prototype.sample;

/**
 * 按订单数量拆分订单的例子
 * 
 * @author mp
 * @date 2013-9-6 上午11:57:59
 */
public class Client {

	public static void main(String[] args) {

		// 创建个人订单对象，这里为了演示简单，直接new了
		PersonalOrder op = new PersonalOrder();
		op.setOrderProductNum(2925);
		op.setCustomerName("张三");
		op.setProductId("p001");

		// 这里获取业务处理的类，也直接new了。
		OrderBusiness ob = new OrderBusiness();
		ob.saveOrder(op);
	}

}
