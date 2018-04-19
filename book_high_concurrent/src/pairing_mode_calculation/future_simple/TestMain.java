package pairing_mode_calculation.future_simple;

/**
 * 测试主函数
 * @author mp
 * @date 2016-7-21 下午5:30:26
 */
public class TestMain {
	public static void main(String[] args) {
		Client client = new Client();
		
		//这里会立即返回对象,但是这个对象的数据是异步（其他线程）设置的
		Data data = client.request("name");
		
		System.out.println("请求完毕,我可以做其他的事了");
		
		try {
			//这里模拟立刻返回对象后又做了很多其他的事
			Thread.sleep(2000);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		//这时候想到要用真实数据了,调用一下
		System.out.println("想起要用数据了,看看准备好了不 " + data.getResult());
	}
}
