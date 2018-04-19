package pairing_mode_calculation.product_consum_blockqueue;

/**
 * 用于在生产者和消费者之间传递的对象
 * 因为其在创建出来,内容和数据只在对象创造的时候初始化一次,其次他就是传递,消费者接收到后就把数据取出来
 * @author mp
 * @date 2016-7-20 下午5:31:01
 */
public final class PCData {
	
	private final int initData;
	
	public PCData (int initData) {
		this.initData = initData;
	}
	
	public PCData (String initData) {
		this.initData = Integer.valueOf(initData);
	}
	
	public int getData () {
		return initData;
	}

	@Override
	public String toString() {
		return "data: " + initData;
	}
	
}
