package com.mp.design.iterator.frame;

/**
 * 数组迭代示例
 * @author mp
 * @date 2013-9-7 下午5:17:21
 */
public class Client {
	
	/**
	 * 这里示意使用迭代器来迭代聚合对象
	 * @author mp
	 * @date 2013-9-7 下午5:17:52
	 * @Description
	 */
	public static void someOperation(){
		String [] namess = {"zhangsan","lisi","wangwu"};
		//创建聚合对象
		Aggregate aggregate = new ConcreteAggregate(namess);
		//循环输出聚合对象中的值
		Iterator it = aggregate.createIterator();
		//首先设置迭代器到第一个元素
		it.first();
		while(!it.isDone()){
			//取出当前元素
			Object obj = it.currentItem();
			System.out.println("this obj == "+obj);
			//如果还没有迭代到最后，哪么就向下迭代一个
			it.next();
		}
	}
	
	public static void main(String[] args) {
		someOperation();
	}
}
