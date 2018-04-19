package com.mp.design.simplefactory.frame;

/**
 * 
 * @author mp
 * @date 2013-9-4 上午11:11:33
 */
public class Factory {
	
	/**
	 * 
	 * @author mp
	 * @date 2013-9-4 上午11:09:09
	 * @param condition
	 * @return
	 * @throws Exception
	 * @Description 
	 *
	 */
	public static Api createApi(int condition){
		
		Api api = null;

		if (condition == 1) {
			api = new ImpA();
		} else if (condition == 2) {
			api = new ImpB();
		}

		return api;
	}

}
