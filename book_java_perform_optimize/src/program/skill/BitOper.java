package program.skill;

public class BitOper {
	
	/**
	 * 采用位运算比传统运算要快3倍
	 * @author mp
	 * @date 2016-6-6 上午11:07:56
	 * @param args
	 * @Description
	 */
	public static void main(String[] args) {
		method1();
		method2();
	}
	
	/**
	 * 传统运算方式
	 * @author mp
	 * @date 2016-6-6 上午11:05:16
	 * @Description
	 */
	private static void method1 () {
		long s = System.currentTimeMillis();
		long a = 100;
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			a*=2;
			a/=2;
		}
		long e = System.currentTimeMillis();
		System.out.println("method1 used-" + (e-s));
	}
	
	/**
	 * 位运算
	 * @author mp
	 * @date 2016-6-6 上午11:10:50
	 * @Description
	 */
	private static void method2 () {
		long s = System.currentTimeMillis();
		long a = 100;
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			a<<=1;//乘以2
			a>>=1;//除以2
		}
		long e = System.currentTimeMillis();
		System.out.println("method2 used-" + (e-s));
	}
	
}
