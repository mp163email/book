package program.skill;

public class Loot {
	
	/**
	 * 误人子弟：*******传统方式比展开方式要快要不就是差不多的速度**********
	 * @author mp
	 * @date 2016-6-6 上午11:57:09
	 * @param args
	 * @Description
	 */
	public static void main(String[] args) {
		method1();
		method2();
	}
	
	/**
	 * 传统方式
	 * @author mp
	 * @date 2016-6-6 上午11:56:52
	 * @Description
	 */
	private static void method1 () {
		long s = System.currentTimeMillis();
		int [] array = new int [9999999];
		int index = 0;
		for (int i = 0; i < 9999999; i++) {
			array[i] = i;
			index ++;
		}
		System.out.println(index);
		long e = System.currentTimeMillis();
		System.out.println("method1 used-" + (e-s));
	}
	
	/**
	 * 展开方式
	 * @author mp
	 * @date 2016-6-6 上午11:57:02
	 * @Description
	 */
	private static void method2 () {
		long s = System.currentTimeMillis();
		int [] array = new int [9999999];
		int index = 0;
		for (int i = 0; i < 9999999; i+=3) {
			array[i] = i;
			array[i + 1] = i + 1;
			array[i + 2] = i + 2;
			index ++;
		}
		System.out.println(index);
		long e = System.currentTimeMillis();
		System.out.println("method2 used-" + (e-s));
	}
	
}
