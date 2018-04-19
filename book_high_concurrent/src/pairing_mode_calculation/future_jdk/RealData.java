package pairing_mode_calculation.future_jdk;

import java.util.concurrent.Callable;

/**
 * Callable一般有返回值,用于线程池submit的提交
 * @author mp
 * @date 2016-7-22 上午10:53:01
 */
public class RealData implements Callable<String>{

	private String para;
	
	public RealData (String para) {
		this.para = para;
	}
	
	@Override
	public String call() throws Exception {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < 10; i++) {
			sb.append(para);
		}
		Thread.sleep(10 * 1000);
		System.out.println("真实数据构造完毕");
		return sb.toString();
	}

}
