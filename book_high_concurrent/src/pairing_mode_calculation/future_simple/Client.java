package pairing_mode_calculation.future_simple;

/**
 * 客户端
 * @author mp
 * @date 2016-7-21 下午5:18:30
 */
public class Client {
	
	/**
	 * 这里的FutureData是直接返回的,但是调用它里边的getResult的时候,需要等待
	 * @author mp
	 * @date 2016-7-21 下午5:21:59
	 * @param queryStr
	 * @return
	 * @Description
	 */
	public Data request (final String queryStr) {
		final FutureData future = new FutureData();
		
		//封装数据是异步的
		new Thread(new Runnable() {
			@Override
			public void run() {
				RealData realData = new RealData(queryStr);
				future.setRealData(realData);
			}
		}).start();
		
		return future;
	}
	
}
