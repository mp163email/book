package com.mp.design.observer.sample;

/**
 * 订阅报纸 示例
 * @author mp
 * @date 2013-9-7 下午4:02:06
 */
public class Client {
	
	public static void main(String[] args) {
		
		//创建一个报纸作为被观察者（目标对象）
		NewsPaper subject = new NewsPaper();
		
		//创建阅读者，也就是观察者
		Reader reader1 = new Reader();
		reader1.setName("name1");
		Reader reader2 = new Reader();
		reader2.setName("name2");
		Reader reader3 = new Reader();
		reader3.setName("name3");
		
		//注册阅读者
		subject.attach(reader1);
		subject.attach(reader2);
		subject.attach(reader3);
		
		//出版报纸
		subject.setContent("本期内容是观察者模式");
	}
}
