package com.mp.design.adapter.sample;

import java.util.ArrayList;
import java.util.List;

/**
 * 日志管理例子,将以前文件存储的接口适配到新接口
 * @author mp
 * @date 2013-9-4 下午6:05:10
 */
public class Client {
	
	public static void main(String[] args) {
		
		//准备日志内容，也就是测试数据
		LogModel lml = new LogModel();
		lml.setLogId("001");
		lml.setOperateUser("admin");
		lml.setOperateTime("2010-03-02 10:08:18");
		lml.setLogContent("这是一个例子");
		
		List<LogModel> list = new ArrayList<LogModel>();
		list.add(lml);

		//创建操作日志的接口对象
		LogFileOPerateApi logFileApi = new LogFileOperate("");
		
		//创建新版操作日志的接口对象
		LogDbOperateApi api = new Adapter(logFileApi);
		
		//保存日志文件
		api.createLog(lml);
		
		//读取日志文件内容
		List<LogModel> readLog = api.getAllLog();
		System.out.println("readLog == "+readLog);
		
		
		
	}
}
