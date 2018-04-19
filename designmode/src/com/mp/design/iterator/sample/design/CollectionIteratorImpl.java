package com.mp.design.iterator.sample.design;

/**
 * 用来实现访问list集合的迭代接口
 * 
 * @author mp
 * @date 2013-9-7 下午6:01:09
 */
public class CollectionIteratorImpl implements Iterator {

	private PayManager aggregate = null;

	private int index = -1;

	public CollectionIteratorImpl(PayManager aggregate) {
		this.aggregate = aggregate;
	}

	@Override
	public void first() {
		index = 0;
	}

	@Override
	public void next() {
		if (index < this.aggregate.size()) {
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
