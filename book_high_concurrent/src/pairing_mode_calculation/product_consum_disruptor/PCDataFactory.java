package pairing_mode_calculation.product_consum_disruptor;

import com.lmax.disruptor.EventFactory;

/**
 * Disruptor在初始化的时候需要一个创建PCData的工厂类
 * @author mp
 * @date 2016-7-21 下午2:28:26
 */
public class PCDataFactory implements EventFactory<PCData> {

	@Override
	public PCData newInstance() {
		return new PCData();
	}
}
