package com.mp.design.iterator.sample.design;

/**
 * 用来实现访问数组的迭代接口
 * @author mp
 * @date 2013-9-7 下午5:57:27
 */
public class ArrayIteratorImpl implements Iterator{

	/**
	 * 用来存放被迭代的聚合对象
	 */
	private SalaryManager aggregate = null;
	
	/**
	 * 内部索引，记录当前迭代到的索引位置
	 */
	private int index = -1;
	
	public ArrayIteratorImpl(SalaryManager aggregate){
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
