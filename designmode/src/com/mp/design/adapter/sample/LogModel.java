package com.mp.design.adapter.sample;

import java.io.Serializable;

/**
 * 日志数据对象
 * @author mp
 * @date 2013-9-4 下午3:52:02
 */
public class LogModel implements Serializable{

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 日志编号
	 */
	private String logId;
	
	/**
	 * 操作人员
	 */
	private String operateUser;
	
	/**
	 * 操作时间
	 */
	private String operateTime;
	
	/**
	 * 日志内容
	 */
	private String logContent;

	public String getLogId() {
		return logId;
	}

	public void setLogId(String logId) {
		this.logId = logId;
	}

	public String getOperateUser() {
		return operateUser;
	}

	public void setOperateUser(String operateUser) {
		this.operateUser = operateUser;
	}

	public String getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(String operateTime) {
		this.operateTime = operateTime;
	}

	public String getLogContent() {
		return logContent;
	}

	public void setLogContent(String logContent) {
		this.logContent = logContent;
	}
}

