package com.mp.design.proxy.sample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;

public class UserManager {

	/**
	 * 根据部门编号来获取该部门下的所有人员
	 * @author mp
	 * @date 2013-9-7 下午3:22:06
	 * @param depId
	 * @return
	 * @throws Exception
	 * @Description
	 */
	public Collection<UserModelApi> getUserByDepId(String depId) throws Exception{
		Collection<UserModelApi> col = new ArrayList<UserModelApi>();
		Connection conn = null;
		try{
			conn = this.getConnection();
			String sql = "select u.userId, u.name from tb1_user u, tb1_dep d where u.depId=d.depId and d.depId like ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, depId+"%");
			ResultSet rs = pstmt.executeQuery();
			
			while(rs.next()){
				//这里是创建的代理对象，而不是直接创建UserModel对象
				Proxy proxy = new Proxy(new UserModel());
				//只设置userId和name两个值就可以了
				proxy.setUserId(rs.getString("userId"));
				proxy.setName(rs.getString("name"));
				col.add(proxy);
			}
			
			rs.close();
			pstmt.close();
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			conn.close();
		}
		return col;
	}
	
	private Connection getConnection() throws Exception{
		Class.forName("你用的数据库对应jdbc驱动类");
		return DriverManager.getConnection("链接数据库的url","用户名","密码");
	}
}
