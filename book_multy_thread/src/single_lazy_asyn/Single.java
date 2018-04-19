package single_lazy_asyn;



public class Single {
	
	/**
	 * 私有构造方法
	 */
	private Single () {
		
	}
	
	/**
	 * 私有静态实例
	 */
	private static Single single = null;
	
	/**
	 * 获取实例
	 * @author mp
	 * @date 2016-5-25 下午2:08:02
	 * @return
	 * @Description
	 */
	public static Single getInstance () {
		if (single == null) {
			return new Single();
		}
		return single;
	}
	
}
