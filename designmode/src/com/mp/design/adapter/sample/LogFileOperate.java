package com.mp.design.adapter.sample;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class LogFileOperate implements LogFileOPerateApi {

	/**
	 * 日志文件的路径和名称
	 */
	private String logFilePathName = "src/com/mp/design/adapter/sample/test.log";

	public LogFileOperate(String logFilePathName) {
		if (logFilePathName != null && logFilePathName.length() > 0) {
			this.logFilePathName = logFilePathName;
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<LogModel> readLogFile() {
		List<LogModel> list = null;
		ObjectInputStream oin = null;

		try {
			File file = new File(logFilePathName);
			if (file.exists()) {
				oin = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
				list = (List<LogModel>) oin.readObject();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				if(oin != null){
					oin.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}

		return list;
	}

	@Override
	public void writeLogFile(List<LogModel> list) {
		File file = new File(logFilePathName);
		ObjectOutputStream oout = null;
		try {
			oout = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
			oout.writeObject(list);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				oout.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

}
