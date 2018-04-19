package program.skill;

public class Variable {
	
	private static int x = 0;

	/**
	 * 在对变量做运算时,局部变量要比静态变量快一半以上  能使用局部变量的一定要是用局部变量
	 * @author mp
	 * @date 2016-6-6 上午11:03:04
	 * @param args
	 * @Description
	 */
	public static void main(String[] args) {
		method1();
		method2();
	}
	
	/**
	 * 使用局部变量
	 * @author mp
	 * @date 2016-6-6 上午11:00:26
	 * @Description
	 */
	private static void method1 () {
		long s = System.currentTimeMillis();
		int a = 0;
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			a++;
		}
		long e = System.currentTimeMillis();
		System.out.println("method1 used-" + (e-s));
	}
	
	/**
	 * 使用静态变量
	 * @author mp
	 * @date 2016-6-6 上午11:00:55
	 * @Description
	 */
	private static void method2 () {
		long s = System.currentTimeMillis();
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			x++;
		}
		long e = System.currentTimeMillis();
		System.out.println("method2 used-" + (e-s));
	}
	
}
