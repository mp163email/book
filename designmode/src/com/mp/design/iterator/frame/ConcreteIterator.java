package com.mp.design.iterator.frame;

/**
 * 具体的迭代器实现对象，示意的是聚合对象为数组的迭代器
 * @author mp
 * @date 2013-9-7 下午5:05:23
 */
public class ConcreteIterator implements Iterator{

	/**
	 * 持有被迭代的具体的聚合对象
	 */
	private ConcreteAggregate aggregate = null;
	
	/**
	 * 内部索引，记录当前迭代到的索引位置
	 */
	private int index = -1;
	
	public ConcreteIterator(ConcreteAggregate aggregate){
		this.aggregate = aggregate;
	}
	
	@Override
	public void first() {
		index = 0;
	}

	@Override
	public void next() {
		if(index < this.aggregate.size()){
			index = index + 1;
		}
	}

	@Override
	public Object currentItem() {
		return this.aggregate.get(index);
	}

	@Override
	public boolean isDone() {
		if(index == this.aggregate.size()){
			return true;
		}
		return false;
	}
	
}
