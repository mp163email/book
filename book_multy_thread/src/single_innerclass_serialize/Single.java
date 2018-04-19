package single_innerclass_serialize;

import java.io.ObjectStreamException;
import java.io.Serializable;


public class Single implements Serializable {
	
	private static final long serialVersionUID = 7432928681222881677L;

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
	
	/**
	 * 反序列化读的时候会调用
	 * @author mp
	 * @date 2016-5-25 下午2:41:23
	 * @return
	 * @throws ObjectStreamException
	 * @Description
	 */
	protected Object readResolve () throws ObjectStreamException {
		System.out.println("readResolve 被调用");
		return SingleInner.single;
	}
	
}
