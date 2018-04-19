package com.mp.design.iterator.frame;

/**
 * 具体的聚合对象，实现创建相应迭代器对象的功能
 * @author mp
 * @date 2013-9-7 下午5:08:06
 */
public class ConcreteAggregate extends Aggregate{

	/**
	 * 示意，表示聚合对象具体的内容
	 */
	private String [] ss = null;
	
	public ConcreteAggregate(String [] ss){
		this.ss = ss;
	}
	
	@Override
	public Iterator createIterator() {
		//实现创建Iterator的工厂方法
		return new ConcreteIterator(this);
	}
	
	/**
	 * 获取索引所对应的元素
	 * @author mp
	 * @date 2013-9-7 下午5:12:06
	 * @param index
	 * @return
	 * @Description
	 */
	public Object get(int index){
		Object retObj = null;
		if(index < ss.length){
			retObj = ss[index];
		}
		return retObj;
	}
	
	/**
	 * 获取聚合对象的大小
	 * @author mp
	 * @date 2013-9-7 下午5:12:35
	 * @return
	 * @Description
	 */
	public int size(){
		return this.ss.length;
	}

	
}
