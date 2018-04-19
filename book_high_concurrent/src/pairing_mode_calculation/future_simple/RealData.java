package pairing_mode_calculation.future_simple;

/**
 * 真实数据
 * @author mp
 * @date 2016-7-21 下午5:01:19
 */
public class RealData implements Data {
	
	protected final String result;
	
	/**
	 * 模拟构造数据很慢
	 * @param para
	 */
	public RealData (String para) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 10; i++) {
			sb.append(para);
		}
		try {
			Thread.sleep(2000);//模拟构造真实数据很慢
		} catch (Exception e) {
		}
		result = sb.toString();
	}

	@Override
	public String getResult() {
		return result;
	}
	
}
