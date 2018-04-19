package com.mp.design.proxy.sample;

/**
 * 定义用户数据对象的接口
 * @author mp
 * @date 2013-9-7 下午2:56:16
 */
public interface UserModelApi {

	public String getUserId();
	
	public void setUserId(String userId);
	
	public String getName();
	
	public void setName(String name);
	
	public String getDepId();
	
	public void setDepId(String depId);
	
	public String getSex();
	
	public void setSex(String sex);
}
