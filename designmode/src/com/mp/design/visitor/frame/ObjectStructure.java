package com.mp.design.visitor.frame;

import java.util.ArrayList;
import java.util.List;


/**
 * 对象结构，通常在这里对元素对象进行遍历，让访问者能访问到所有的元素
 * @author mp
 * @date 2013-9-8 下午1:58:51
 */
public class ObjectStructure {

	/**
	 * 示意，表示对象结构，可以是一个组合结构或是集合
	 */
	private List<Element> col = new ArrayList<Element>();
	
	/**
	 * 示意方法，提供给客户端操作的高层接口
	 * @author mp
	 * @date 2013-9-8 下午2:02:20
	 * @param visitor
	 * @Description
	 */
	public void handlerRequest(Visitor visitor){
		//循环对象结构中的元素，接受访问
		for(Element ele : col){
			ele.accept(visitor);
		}
	}
	
	public void addElement(Element ele){
		this.col.add(ele);
	}
	
}
