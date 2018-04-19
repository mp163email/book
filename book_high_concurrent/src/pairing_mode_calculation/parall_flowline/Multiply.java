package pairing_mode_calculation.parall_flowline;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 计算(A+B)*B/2中的乘法(A+B)*B
 * @author mp
 * @date 2016-7-22 上午11:38:51
 */
public class Multiply implements Runnable {

	public static BlockingQueue<Msg> bq = new LinkedBlockingQueue<Msg>();

	@Override
	public void run() {
		while (true) {
			try {
				Msg msg = bq.take();
				msg.n = msg.n * msg.j;
				Div.bq.add(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
