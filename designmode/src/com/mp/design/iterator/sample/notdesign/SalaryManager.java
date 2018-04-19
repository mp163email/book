package com.mp.design.iterator.sample.notdesign;

/**
 * 被客户方收购的那个公司的工资管理类
 * @author mp
 * @date 2013-9-7 下午5:30:28
 */
public class SalaryManager {

	/**
	 * 用数组表示
	 */
	private PayModel[] pms = null;
	
	/**
	 * 获取工资列表
	 * @author mp
	 * @date 2013-9-7 下午5:32:13
	 * @return
	 * @Description
	 */
	public PayModel[] getPays(){
		return pms;
	}
	
	/**
	 * 计算工资，其实应该有很多参数，为了演示从简
	 * @author mp
	 * @date 2013-9-7 下午5:32:32
	 * @Description
	 */
	public void calcSalary(){
	
		//计算工资，并把工资信息填充到工资列表中
		//为了测试，输入些数据进去
		PayModel payModel = new PayModel();
		payModel.setPay(3800);
		payModel.setUserName("zhangsan");
		
		PayModel payMode2 = new PayModel();
		payMode2.setPay(5800);
		payMode2.setUserName("lisi");
		
		pms = new PayModel[2];
		pms[0] = payModel;
		pms[1] = payMode2;
	}
}
