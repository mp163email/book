package pairing_mode_calculation.parall_flowline;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 计算(A+B)*B/2中的加法(A+B)
 * @author mp
 * @date 2016-7-22 上午11:20:38
 */
public class Plus implements Runnable {

	public static BlockingQueue<Msg> bq = new LinkedBlockingQueue<Msg>();
	
	@Override
	public void run() {
		while (true) {
			try {
				Msg msg = bq.take();//有数据就取出来,没数据就阻塞
				msg.n = msg.i + msg.j;
				Multiply.bq.add(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
