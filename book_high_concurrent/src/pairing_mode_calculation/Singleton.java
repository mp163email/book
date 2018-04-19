package pairing_mode_calculation;

/**
 * 最优的单例模式
 * 核心是：私有的内部静态类,私有的静态的外部对象
 * @author mp
 * @date 2016-7-20 下午4:32:14
 */
public class Singleton {
	
	/**
	 * 私有构造方法
	 */
	private Singleton () {
		
	}
	
	/**
	 * 私有的内部静态类
	 * @author mp
	 * @date 2016-7-20 下午4:33:22
	 */
	private static class SingletonHolder {
		private static Singleton instance = new Singleton();//在内部类里实例化一个私有的静态的外部类对象
	}
	
	/**
	 * 公共方法-获取内部静态类的外部类对象
	 * @author mp
	 * @date 2016-7-20 下午4:35:33
	 * @return
	 * @Description
	 */
	public static Singleton getInstance () {
		return SingletonHolder.instance;
	}
	
}
