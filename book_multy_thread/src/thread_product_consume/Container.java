package thread_product_consume;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 生产容器
 * @author mp
 * @date 2016-4-26 下午1:48:27
 */
public class Container {

	private List<String> strList = new ArrayList<>();
	
	private int sleepMill;
	
	public Container (int sleepMill) {
		this.sleepMill = sleepMill;
	}
	
	/**
	 * 放入容器,这个地方必须要有同步关键字,因为ArrayList是线程不安全的
	 * @author mp
	 * @date 2016-4-26 下午1:49:52
	 * @Description
	 */
	synchronized public void put () {
		try {
			while (strList.size() >= 1) {
				this.wait();
			}
			String putStr = Math.random() + "";
			strList.add(putStr);
			System.out.println(Thread.currentThread().getName() + "  生产了1个  " + putStr);
			this.notifyAll();
			TimeUnit.MILLISECONDS.sleep(sleepMill);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 从容器中取
	 * @author mp
	 * @date 2016-4-26 下午1:50:14
	 * @Description
	 */
	synchronized public void get () {
		try {
			while (strList.size() <= 0) {
				this.wait();
			}
			String getStr = strList.get(0);
			strList.remove(0);
			System.out.println(Thread.currentThread().getName() + "  消费了1个  " + getStr);
			this.notifyAll();
			TimeUnit.MILLISECONDS.sleep(sleepMill);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
