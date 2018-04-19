package com.mp.design.proxy.sample;

import java.util.Collection;

/**
 * 减少数据库查询示例
 * @author mp
 * @date 2013-9-7 下午3:27:47
 */
public class Client {

	public static void main(String[] args) throws Exception{
		UserManager userManager = new UserManager();
		Collection<UserModelApi> collection = userManager.getUserByDepId("0101");
		
		//如果只是显示用户名称，则不需要重新查询数据库
		for(UserModelApi umApi : collection){
			System.out.println("用户编号："+umApi.getUserId()+", 用户名："+umApi.getName());
		}
		
		//如果访问非用户编号和用户姓名外的属性，那就会重新查询数据库
		for(UserModelApi umAPi : collection){
			System.out.println("用户编号："+umAPi.getUserId()+",用户名："+umAPi.getName()+",所属部门："+umAPi.getDepId());
		}
	}
}
