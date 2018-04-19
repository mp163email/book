package com.mp.design.adapter.sample;

import java.util.ArrayList;
import java.util.List;

/**
 * 适配器对象，将记录日志到文件的功能适配成第二版需要的增删改查功能
 * @author mp
 * @date 2013-9-5 上午10:33:37
 */
public class Adapter implements LogDbOperateApi{
	
	/**
	 * 持有需要被适配的接口对象
	 */
	private LogFileOPerateApi adaptee;

	/**
	 * 构造方法
	 * @param adaptee
	 */
	public Adapter(LogFileOPerateApi adaptee){
		this.adaptee = adaptee;
	}
	
	@Override
	public void createLog(LogModel lm) {
		
		//1.先读取文件内容
		List<LogModel> list = adaptee.readLogFile();
		if(list == null){
			list = new ArrayList<LogModel>();
		}
		
		
		//2.加入新的日志对象
		list.add(lm);
		//3.重新写入文件
		adaptee.writeLogFile(list);
		
		/**以下处理数据的insert操作*/
		
	}

	@Override
	public void updateLog(LogModel lm) {
		
		//1.先读取文件的内容
		List<LogModel> list = adaptee.readLogFile();
		//2.修改相应的日志对象
		for(int i = 0; i < list.size(); i++){
			if(list.get(i).getLogId().equals(lm.getLogId())){
				list.set(i, lm);
				break;
			}
		}
		//3.重新写入文件
		adaptee.writeLogFile(list);
		
		
		/**以下处理数据的update操作*/
	}

	@Override
	public void removeLog(LogModel lm) {
		
		//1.先读取文件的内容
		List<LogModel> list = adaptee.readLogFile();
		//2.删除相应的日志对象
		list.remove(lm);
		//3.重新写入文件
		adaptee.writeLogFile(list);
		
		/**以下处理数据的update操作*/
	}

	@Override
	public List<LogModel> getAllLog() {
		return adaptee.readLogFile();
	}

	
}
