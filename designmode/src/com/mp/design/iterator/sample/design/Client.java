package com.mp.design.iterator.sample.design;

/**
 * 两个公司工资表数据的整合 示例
 * 
 * @author mp
 * @date 2013-9-7 下午6:04:16
 */
public class Client {

	public static void main(String[] args) {

		// 访问集团的工资列表
		PayManager payManager = new PayManager();
		// 先计算在获取
		payManager.calcPay();
		System.out.println("集体公司列表");
		test(payManager.createIterator());

		// 访问新公司的工资列表
		SalaryManager salaryManager = new SalaryManager();
		// 先计算在获取
		salaryManager.calcSalary();
		System.out.println("新收购的公司工资列表");
		test(salaryManager.createIterator());
	}

	public static void test(Iterator it) {
		// 循环输出聚合对象中的值
		// 首先设置迭代器到第一个元素
		it.first();
		while (!it.isDone()) {
			// 取出当前元素
			Object obj = it.currentItem();
			System.out.println("this obj == " + obj);
			// 如果没有迭代到最后,就向下迭代一个
			it.next();
		}
	}

}
