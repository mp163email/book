package pairing_mode_calculation.parall_sort;

import java.util.Arrays;

/**
 * 复习一下冒泡排序
 * @author mp
 * @date 2016-7-25 下午3:43:22
 */
public class Serial_BubbleSort {

	private static int [] arrays = {5, 2, 3, 10, 1, 100};
	
	/**
	 * 主函数
	 * @author mp
	 * @date 2016-7-25 下午3:45:31
	 * @param args
	 * @Description
	 */
	public static void main(String[] args) {
		print();
		for (int i = arrays.length - 1; i > 0; i--) {//外层循环控制两两比较的次数，不断递减
			for (int j = 0; j < i; j++) {//内层循环控制范围内相邻两数的大小比较
				if (arrays[j] > arrays[j + 1]) {
					int num = arrays[j];
					arrays[j] = arrays[j + 1];
					arrays[j + 1] = num;
				}
			}
		}
		print();
	}
	
	/**
	 * 打印数组
	 * @author mp
	 * @date 2016-7-25 下午3:45:17
	 * @Description
	 */
	public static void print() {
		System.out.println(Arrays.toString(arrays));
	}
	
}
