package program.skill;

public class OneOrTwoArray {
	
	/**
	 * 误人子弟******二维数组要比一维数组要快,快一倍左右**********
	 * @author mp
	 * @date 2016-6-6 上午11:50:08
	 * @param args
	 * @Description
	 */
	public static void main(String[] args) {
		method1();
		method2();
	}
	
	/**
	 * 一维数组赋值读取
	 * @author mp
	 * @date 2016-6-6 上午11:47:52
	 * @Description
	 */
	private static void method1 () {
		long s = System.currentTimeMillis();
		int [] array = new int[100*10000];
		int re = 0;
		int size = array.length;
		for (int k = 0; k < 100; k++) {//100次对数组从1-100*10000的赋值
			for (int i = 0; i < size; i++) {
				array[i] = i;
			}
		}
		
		for (int k = 0; k < 100; k++) {//100次对数组从1-100*10000的赋值
			for (int i = 0; i < size; i++) {
				re = array[i];
			}
		}
		long e = System.currentTimeMillis();
		System.out.println("method1 used-" + (e-s));
	}
	
	/**
	 * 二维数组赋值读取
	 * @author mp
	 * @date 2016-6-6 上午11:48:05
	 * @Description
	 */
	private static void method2 () {
		long s = System.currentTimeMillis();
		int [] [] array = new int [1000] [1000];
		int re = 0;
		int size = array.length;//横坐标
		int size1 = array[0].length;//横坐标某一点上的纵坐标
		for (int k = 0; k < 100; k++) {
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size1; j++) {
					array[i][j] = i;
				}
			}
		}
		
		for (int k = 0; k < 100; k++) {
			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size1; j++) {
					re = array[i][j];
				}
			}
		}
		
		long e = System.currentTimeMillis();
		System.out.println("method2 used-" + (e-s));
	}
}
