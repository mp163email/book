package pairing_mode_calculation.product_consum_disruptor;

import java.text.MessageFormat;

import com.lmax.disruptor.WorkHandler;

/**
 * 消费者
 * @author mp
 * @date 2016-7-21 下午2:21:24
 */
public class Consumer implements WorkHandler<PCData> {

	/**
	 * onEvent封装了从环形队列缓冲区中取对象的操作,直接通过这个方法,就把取出的对象直接当做参数传递给方法了,是不是省了很多事*^_^*
	 */
	@Override
	public void onEvent(PCData event) throws Exception {
		long value = event.getValue();
		long sum = value * value;
		System.out.println(Thread.currentThread().getId() + " get data :" + MessageFormat.format("{0} * {1} = {2}", value, value, sum));
	}
	
}
