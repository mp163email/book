package pairing_mode_calculation.parall_flowline;

/**
 * 主函数
 * @author mp
 * @date 2016-7-22 上午11:45:41
 */
public class TestMain {
	
	public static void main(String[] args) {
		
		//开启流水线计算线程
		new Thread(new Plus()).start();
		new Thread(new Multiply()).start();
		new Thread(new Div()).start();
		
		//赋值
		for (int i = 1; i < 100; i++) {//模拟A的值
			for (int j = 1; j < 100; j++) {//模拟B的值
				Msg msg = new Msg();
				msg.i = i;
				msg.j = j;
				Plus.bq.add(msg);
			}
		}
	}
	
}
