package com.mp.design.proxy.sample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * 代理对象，代理用户数据对象
 * @author mp
 * @date 2013-9-7 下午3:01:04
 */
public class Proxy implements UserModelApi{

	/**
	 * 持有被代理的具体的目标对象
	 */
	private UserModel realSubject = null;
	
	public Proxy(UserModel userModel){
		this.realSubject = userModel;
	}
	
	/**
	 * 标示是否已经重新装载过数据了
	 */
	private boolean loaded = false;
	
	@Override
	public String getUserId() {
		return realSubject.getUserId();
	}

	@Override
	public void setUserId(String userId) {
		realSubject.setUserId(userId);
	}

	@Override
	public String getName() {
		return realSubject.getName();
	}

	@Override
	public void setName(String name) {
		realSubject.setName(name);
	}

	@Override
	public String getDepId() {
		
		//需要判断是否已经装载过了
		if(!this.loaded){
			reload();
			//设置重新装载的标志为true
			this.loaded = true;
		}
		return realSubject.getDepId();
	}

	@Override
	public void setDepId(String depId) {
		realSubject.setDepId(depId);
	}

	@Override
	public String getSex() {
		
		if(!this.loaded){
			reload();
			this.loaded = true;
		}
		return realSubject.getSex();
	}

	@Override
	public void setSex(String sex) {
		realSubject.setSex(sex);
	}

	/**
	 * 重新查询数据库以获取完整的用户数据
	 * @author mp
	 * @date 2013-9-7 下午3:09:51
	 * @Description
	 */
	private void reload(){
		System.out.println("重新查询数据库获取完整的用户数据，userId=="+realSubject.getUserId());
		Connection conn = null;
		try {
			conn = this.getConnection();
			String sql = "select * from tb1_user where userId=?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1,realSubject.getUserId());
			ResultSet rs = pstmt.executeQuery();
			if(rs.next()){
				//只需要重新获取除了userId和name外的数据
				realSubject.setDepId(rs.getString("depId"));
				realSubject.setSex(rs.getString("sex"));
			}
			
			rs.close();
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				conn.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}
	
	public String toString(){
		return "userId="+getUserId()+",name="+getName()+",sex="+getSex()+",depId="+getDepId();
	}
	
	
	private Connection getConnection() throws Exception{
		Class.forName("你用的数据库对应jdbc驱动类");
		return DriverManager.getConnection("链接数据库的url","用户名","密码");
	}
}
