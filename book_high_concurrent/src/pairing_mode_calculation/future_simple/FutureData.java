package pairing_mode_calculation.future_simple;

/**
 * FutureData-立刻返回给客户端的
 * @author mp
 * @date 2016-7-21 下午4:59:30
 */
public class FutureData implements Data {

	protected RealData realData = null;
	
	protected boolean isRead = false;//是否数据已经准备好
	
	/**
	 * 放数据的时候notify
	 * @author mp
	 * @date 2016-7-21 下午5:17:41
	 * @param realData
	 * @Description
	 */
	synchronized public void setRealData (RealData realData) {
		if (isRead) {
			return;
		}
		this.realData = realData;
		isRead = true;
		notifyAll();
	}
	
	/**
	 * 获取结果,如果结果没准备好就等待一下这个线程放数据,否则直接返回结果
	 */
	@Override
	synchronized public String getResult() {
		while (!isRead) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return realData.getResult();
	}

}
