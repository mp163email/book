package com.mp.design.interpreter.frame;


/**
 * 抽象表达式
 * @author mp
 * @date 2013-9-8 下午1:09:07
 */
public abstract class AbstractExpression {

	/**
	 * 解释的操作
	 * @author mp
	 * @date 2013-9-8 下午1:10:03
	 * @param ctx
	 * @Description
	 */
	public abstract void interpret(Context ctx);
}
