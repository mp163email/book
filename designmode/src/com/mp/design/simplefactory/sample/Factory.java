package com.mp.design.simplefactory.sample;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Factory {

	private static Factory factory = null;
	
	private static Properties prop = new Properties();
	
	private static InputStream input = null;

	private Factory() {
		loadSourse();
	}
	
	/**
	 * 装配资源
	 * @author mp
	 * @date 2013-9-4 上午11:16:30
	 * @Description
	 */
    private static void loadSourse(){
    	try {
			input = Factory.class.getResourceAsStream("/com/mp/design/simplefactory/sample/factory.properties");
			prop.load(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
    /**
     * 获取单例实例
     * @author mp
     * @date 2013-9-4 上午11:25:10
     * @return
     * @Description 供外部调用使用
     */
	public static Factory instance() {
		if (factory == null) {
			factory = new Factory();
		}
		return factory;
	}

	/**
	 * 获取Api实例
	 * @author mp
	 * @date 2013-9-4 上午11:25:43
	 * @param className
	 * @return
	 * @Description
	 */
	public Api createApi(String className) {

		Api api = null;
		
		try {
			api = (Api) Class.forName(prop.getProperty(className)).newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return api;
	}
}
