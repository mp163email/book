package pairing_mode_calculation;

/**
 * 不变模式
 * 核心是：类和字段都是final, 类不能有子类, 字段不可被修改
 *        字段只在构造方法时赋值一次,不能再修改
 * 应用场景：1.当对象创建后,其内部状态和数据不在发生变化,即不会再被修改.  2.对象被多线程共享,频繁访问
 * 常用的包装类：String, Integer, Boolean, Float, Double等都是不变模式,其所有实例的方法都不需要进行同步操作,保证了他们在多线程下的性能
 * 其实我对上面的这句话,有点不理解,模模糊糊吧
 * @author mp
 * @date 2016-7-20 下午4:42:30
 */
public final class NoChange {
	
	private final int id;
	
	private final String name;
	
	public NoChange (int id, String name) {
		this.id = id;
		this.name = name;
	}

	
	
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
}
