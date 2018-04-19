package com.mp.design.iterator.sample.notdesign;

import java.util.ArrayList;
import java.util.List;

/**
 * 客户方已有的工资管理对象
 * @author mp
 * @date 2013-9-7 下午5:26:07
 */
public class PayManager {

	/**
	 * 用list表示
	 */
	private List list = new ArrayList();
	
	/**
	 * 获取工资列表
	 * @author mp
	 * @date 2013-9-7 下午5:27:20
	 * @return
	 * @Description
	 */
	public List getPayList(){
		return list;
	}
	
	/**
	 * 计算工资，其实应该有参数，为了演示从简
	 * @author mp
	 * @date 2013-9-7 下午5:27:53
	 * @Description
	 */
	public void calcPay(){
		
		//计算工资，并把工资信息填充到工资列表中
		//为了测试，输入些数据进去
		PayModel payModel = new PayModel();
		payModel.setPay(3800);
		payModel.setUserName("zhangsan");
		
		PayModel payMode2 = new PayModel();
		payMode2.setPay(5800);
		payMode2.setUserName("lisi");
		
		list.add(payModel);
		list.add(payMode2);
	}
	
}
