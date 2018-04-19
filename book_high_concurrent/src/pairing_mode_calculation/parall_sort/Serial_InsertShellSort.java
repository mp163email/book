package pairing_mode_calculation.parall_sort;

import java.util.Arrays;

/**
 * 串行-希尔排序（变种的插入排序）
 * 将数组按指定的间隔分成不同的子数组,并分别对他们进行插入排序,排完一次,分割数减一,再对每个分割的子数组进行插入排序,直到分割数为1,进行最后一次插入排序
 * 核心思想是分割数组,使子数组之间互不相关,优点是即使一个较小的元素在数组末尾，由于每次元素移动都以h为间隔进行,因此它能在很少的交换次数下,就被换到最接近最终位置的地方
 * @author mp
 * @date 2016-7-26 下午12:00:06
 */
public class Serial_InsertShellSort {

	private static int [] arrays = {10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
	
	/**
	 * 主函数
	 * @author mp
	 * @date 2016-7-26 下午1:52:10
	 * @param args
	 * @Description
	 */
	public static void main(String[] args) {
//		print();
		int h = 2;//设置为3,不断递减,为了验证自己的想法是否正确,通过验证是正确的
		
//		while (h <= arrays.length / 3) {//计算一个合理的h
//			h = h * 3 + 1;
//		}
		while (h > 0) {
			System.out.println("h = " + h);
			for (int i = h; i < arrays.length; i++) {
				if (arrays[i] < arrays[i - h]) {
					int tmp = arrays[i];
					int j = i - h;
					while (j >= 0 && arrays[j] > tmp) {
						arrays[j + h] = arrays[j];
						j -= h;
					}
					arrays[j + h] = tmp;
				}
//				print();
			}
//			h = (h - 1) / 3;//计算下一个合理的h
			h --;
			print();
		}
//		print();
	}
	
	/**
	 * 打印数组
	 * @author mp
	 * @date 2016-7-26 下午1:52:18
	 * @Description
	 */
	public static void print() {
		System.out.println(Arrays.toString(arrays));
	}
	
}
