package com.mp.design.prototype.sample;

/**
 * 创建订单的业务对象
 * 
 * @author mp
 * @date 2013-9-6 上午11:51:51
 */
public class OrderBusiness {

	/**
	 * 创建订单的方法
	 * 
	 * @author mp
	 * @date 2013-9-6 上午11:52:15
	 * @param order
	 * @Description
	 */
	public void saveOrder(OrderApi order) {

		// 1.判断当前的预订产品数量是否大于1000
		while (order.getOrderProductNum() > 1000) {
			// 2.如果大于，还需要继续拆分
			// 2-1.再新建一份订单，跟传入的订单除了数量不一样外，其他都相同
			OrderApi newOrder = order.cloneOrder();
			newOrder.setOrderProductNum(1000);
			order.setOrderProductNum(order.getOrderProductNum() - 1000);

			// 然后是业务功能处理了，省略了，打印输出，看一下
			System.out.println("拆分成的订单==" + newOrder);
		}
		// 3.不超过，那就直接业务功能窗户里，胜率了，打印输出
		System.out.println("订单==" + order);
	}
}
