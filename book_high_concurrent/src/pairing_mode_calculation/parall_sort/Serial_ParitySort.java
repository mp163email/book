package pairing_mode_calculation.parall_sort;

import java.util.Arrays;

/**
 * 串行-奇偶排序
 * 核心思想也是分割数组
 * 首先确保奇偶至少执行一次
 * 然后因为不论奇偶索引比较，都是相邻两个数比较，并且把小的放到左边,所以，如果偶索引比较没有把整个数组排序完,当进行奇索引排序的时候,必然会检测到有交换
 * 结论：从偶索引比较开始,至少进行过一次奇偶交换,当为奇索引比较(成对出现)并且没有数据交换则排序完成
 * @author mp
 * @date 2016-7-25 下午4:57:27
 */
public class Serial_ParitySort {
	
	private static int [] arrays = {5, 2, 3, 10, 1, 100};
	
	public static void main(String[] args) {
		
		print();
		
		int exchange = 1;//默认有交换
		int startIndex = 0;//0-偶索引比较,1-奇索引比较,默认从偶索引比较开始
		while (exchange == 1 || startIndex == 1) {//这样至少会执行一次奇偶成对的比较(从偶开始,然后startIndex变成0,进行奇比较)
			exchange = 0;
			for (int i = startIndex; i < arrays.length - 1; i+=2) {
				if (arrays[i] > arrays[i + 1]) {
					int tmp = arrays[i];
					arrays[i] = arrays[i + 1];
					arrays[i + 1] = tmp;
					exchange = 1;
				}
			}
			if (startIndex == 0) {
				startIndex = 1;
			} else {
				startIndex = 0;
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
