package single_innerclass_sync;



public class Single {
	
	/**
	 * 私有构造方法
	 */
	private Single () {
		
	}
	
	/**
	 * 静态内部类
	 * @author mp
	 * @date 2016-5-25 下午2:22:44
	 */
	private static class SingleInner {
		private static Single single = new Single();
	}
	
	/**
	 * 获取实例
	 * @author mp
	 * @date 2016-5-25 下午2:08:02
	 * @return
	 * @Description
	 */
	public static Single getInstance () {
		return SingleInner.single;
	}
	
}
