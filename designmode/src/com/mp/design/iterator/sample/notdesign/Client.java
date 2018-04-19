package com.mp.design.iterator.sample.notdesign;

import java.util.Iterator;
import java.util.List;

/**
 * 两个公司工资表数据的整合 示例
 * @author mp
 * @date 2013-9-7 下午5:34:40
 */
public class Client {

	public static void main(String[] args) {
		
		//访问集团的工资列表
		PayManager payManager = new PayManager();
		//先计算在获取
		payManager.calcPay();
		List payList = payManager.getPayList();
		Iterator it = payList.iterator();
		System.out.println("集团公司列表");
		while(it.hasNext()){
			PayModel pm = (PayModel)it.next();
			System.out.println(pm);
		}
		
		//访问新公司的工资列表
		SalaryManager salaryManager = new SalaryManager();
		//先计算在获取
		salaryManager.calcSalary();
		PayModel[] pms = salaryManager.getPays();
		System.out.println("新收购的公司工资列表");
		for(int i = 0; i < pms.length; i++){
			System.out.println(pms[i]);
		}
	}
}
