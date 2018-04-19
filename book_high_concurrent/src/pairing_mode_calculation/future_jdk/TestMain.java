package pairing_mode_calculation.future_jdk;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * 主函数
 * @author mp
 * @date 2016-7-22 上午11:01:38
 */
public class TestMain {
	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		FutureTask<String> future = new FutureTask<String>(new RealData("a"));//构造方法里可以放一个Callable实现类,具体要执行的应用逻辑,在这里是获取真正的值,也就是要异步交由线程池执行的逻辑
		ExecutorService executor = Executors.newCachedThreadPool();
		executor.submit(future);//FutureTask内部也实现了Runnalbe接口,所以可以交由线程池执行
		System.out.println("数据提交完毕");
		Thread.sleep(5 * 1000);//这里模拟获得了返回值后,继续做别的事
		System.out.println("开始获取数据,有结果立刻返回,没有等待");
		System.out.println(future.get());//获取真正的值,有立刻返回,没有等待
	}
	
}
