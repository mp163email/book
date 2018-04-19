package pairing_mode_calculation.parall_sort;

import java.util.Arrays;


/**
 * 串行-插入排序
 * 把第一个元素当做已经排序好的，然后从第二个元素开始跟前边的做比较,满足条件就换位
 * @author mp
 * @date 2016-7-26 上午9:56:12
 */
public class Serial_InsertSort {
	
	private static int [] arrays = {5, 52, 6, 3, 4};
	
	/**
	 * 主函数
	 * @author mp
	 * @date 2016-7-26 上午9:57:42
	 * @param args
	 * @Description
	 */
	public static void main(String[] args) {
		print();
		int length = arrays.length;
		int j, i, key;
		for (i = 1; i < length; i++) {//从第二个元素开始
			key = arrays[i];
			j = i - 1;
			while (j >= 0 && arrays[j] > key) {//比较并换位
				arrays[j + 1] = arrays[j];
				j --;
			}
			arrays[j + 1] = key;//正好是挪数据的那个位子
//			print();
		}
		print();
	}
	
	/**
	 * 打印数组
	 * @author mp
	 * @date 2016-7-26 上午9:57:11
	 * @Description
	 */
	public static void print() {
		System.out.println(Arrays.toString(arrays));
	}
}
