package program.skill;

public class TryCatch {
	
	/**
	 * 误人子弟,*************得到的结论与书中恰好相反,将try-catch放到循环里边反而会更快，而且快很多********
	 * @author mp
	 * @date 2016-6-6 上午10:56:48
	 * @param args
	 * @Description
	 */
	public static void main(String[] args) {
		method2();
		method3();
		method1();
	}
	
	/**
	 * try-catch在循环里边
	 * @author mp
	 * @date 2016-6-6 上午10:32:16
	 * @Description
	 */
	private static void method1 () {
		long s = System.currentTimeMillis();
		int a = 0;
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			try {
				a++;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println(a);
		long e = System.currentTimeMillis();
		System.out.println("method1 used-" + (e-s));
	}
	
	/**
	 * try-catch在循环外边
	 * @author mp
	 * @date 2016-6-6 上午10:32:37
	 * @Description
	 */
	private static void method2 () {
		long s = System.currentTimeMillis();
		try {
			int a = 0;
			for (int i = 0; i < Integer.MAX_VALUE; i++) {
				a++;
			}
			System.out.println(a);
		} catch (Exception e) {
			e.printStackTrace();
		}
		long e = System.currentTimeMillis();
		System.out.println("method2 used-" + (e-s));
	}
	
	/**
	 * 不加try-catch
	 * @author mp
	 * @date 2016-6-6 上午10:56:09
	 * @Description
	 */
	private static void method3 () {
		long s = System.currentTimeMillis();
		int a = 0;
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			a++;
		}
		System.out.println(a);
		long e = System.currentTimeMillis();
		System.out.println("method3 used-" + (e-s));
	}
	
}
