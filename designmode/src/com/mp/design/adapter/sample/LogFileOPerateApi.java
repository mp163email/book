package com.mp.design.adapter.sample;

import java.util.List;

/**
 * 日志文件操作接口
 * @author mp
 * @date 2013-9-4 下午5:50:08
 */
public interface LogFileOPerateApi {
	
	/**
	 * 读取日志文件，从文件里面获取存储的日志列表对象
	 * @author mp
	 * @date 2013-9-4 下午5:50:23
	 * @return
	 * @Description
	 */
	public List<LogModel> readLogFile();
	
	/**
	 * 写日志文件，把日志列表写出到日志文件中
	 * @author mp
	 * @date 2013-9-4 下午5:50:56
	 * @param list
	 * @Description
	 */
	public void writeLogFile(List<LogModel> list);
	
}
