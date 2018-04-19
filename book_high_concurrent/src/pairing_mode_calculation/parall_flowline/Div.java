package pairing_mode_calculation.parall_flowline;

import java.text.MessageFormat;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 计算(A+B)*B/2中的除法(A+B)*B/2
 * @author mp
 * @date 2016-7-22 上午11:40:20
 */
public class Div implements Runnable {

	public static BlockingQueue<Msg> bq = new LinkedBlockingQueue<Msg>();

	@Override
	public void run() {
		try {
			while (true) {
				Msg msg = bq.take();
				msg.n = msg.n / 2;
				System.out.println(MessageFormat.format("({0} + {1}) * {2} / 2 = {3}", msg.i, msg.j, msg.j, msg.n));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
