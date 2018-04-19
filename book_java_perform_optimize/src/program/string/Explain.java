package program.string;

public class Explain {
	/**
	 * String内部由 char数组, offset偏移量, count长度三部分组成 （本质上就是一个char型的数组）
	 * 
	 * String有三大特性：
	 * 	1.不变性：一旦生成,不能再对他修改。好处：当被多线程共享且频繁访问时,可以省略同步和锁等待的时间,从而提升系统性能
	 *  2.常量池：当不同字符串对象有相同的值时,他们值引用常量池的同一个拷贝
	 *  3.类的final定义,不会有子类
	 * 
	 */
}
