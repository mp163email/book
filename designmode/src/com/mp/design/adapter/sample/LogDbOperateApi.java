package com.mp.design.adapter.sample;

import java.util.List;

/**
 * 定义操作日志的应用接口，为实例简单，只定义了增删改查的方法
 * @author mp
 * @date 2013-9-5 上午10:29:22
 */
public interface LogDbOperateApi {

	/**
	 * 新增日志
	 * @author mp
	 * @date 2013-9-5 上午10:30:46
	 * @param lm
	 * @Description
	 */
	public void createLog(LogModel lm);
	
	/**
	 * 修改日志
	 * @author mp
	 * @date 2013-9-5 上午10:31:14
	 * @param lm
	 * @Description
	 */
	public void updateLog(LogModel lm);
	
	/**
	 * 删除日志
	 * @author mp
	 * @date 2013-9-5 上午10:31:44
	 * @param lm
	 * @Description
	 */
	public void removeLog(LogModel lm);
	
	/**
	 * 获取所有的日志
	 * @author mp
	 * @date 2013-9-5 上午10:32:10
	 * @return
	 * @Description
	 */
	public List<LogModel> getAllLog();
}
