package program.skill;

public class ArrayCopy {
	
	/**
	 * 传统方式跟System.copyarray后者对比前者并没有显著性能上提升,但建议用后者,现成的方法,native类型的
	 * @author mp
	 * @date 2016-6-6 下午12:07:50
	 * @param args
	 * @Description
	 */
	public static void main(String[] args) {
		method1();
		method2();
	}
	
	/**
	 * 传统方式复制数组
	 * @author mp
	 * @date 2016-6-6 下午12:04:03
	 * @Description
	 */
	private static void method1 () {
		int size = 1000000;
		int [] array = new int [size];
		int [] arraydst = new int [size];
		for (int i = 0; i < array.length; i++) {
			array[i] = i;
		}
		
		long s = System.currentTimeMillis();
		for (int k = 0; k < 10000; k++) {
			for (int i = 0; i < size; i++) {
				arraydst[i] = array[i];
			}
		}
		long e = System.currentTimeMillis();
		System.out.println("method1 used-" + (e-s));
	}
	
	/**
	 * System.arraycopy方法复制数组
	 * @author mp
	 * @date 2016-6-6 下午12:04:21
	 * @Description
	 */
	private static void method2 () {
		int size = 1000000;
		int [] array = new int [size];
		int [] arraydst = new int [size];
		for (int i = 0; i < array.length; i++) {
			array[i] = i;
		}
		
		long s = System.currentTimeMillis();
		for (int k = 0; k < 10000; k++) {
			System.arraycopy(array, 0, arraydst, 0, size);
		}
		long e = System.currentTimeMillis();
		System.out.println("method2 used-" + (e-s));
	}
	
}
