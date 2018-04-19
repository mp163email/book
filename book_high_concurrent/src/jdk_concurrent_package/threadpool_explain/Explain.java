package jdk_concurrent_package.threadpool_explain;

/**
 * 常用线程池的解释
 * @author mp
 * @date 2016-7-13 上午10:19:36
 */
public class Explain {
	/**
	 * 使用哪种线程池,要考虑两个因素，一个是机器的线程资源是否紧张,一个是任务量和单个任务的执行时间
	 * 
	 * a.如果线程资源紧张（cpu，mem),要用固定线程数的线程池newFiexedThreadPool
	 * b.如果线程资源不紧张,且任务提交频繁,单个任务执行时间短,就用newCachedThreadPool,因为对线程处理任务效率会高
	 * 
	 * 1.newFixedThreadPool() 返回一个corePoolSize和maximumPoolSize相等的，使用无界的任务队列LinkedBlockingQueue的线程池
	 * 无界队列里存放无法立刻执行的任务,当任务提交非常频繁的时候,该队列会迅速膨胀,从而耗尽系统资源
	 * 也就是说在任务提交不频繁的时候,可以使用newFixedThreadPool线程池
	 * 
	 * 2.newCachedThreadPool()返回的是一个corePoolSize为0, maximumPoolSize无穷大，使用直接提交队列synchronousQueue的线程池
	 * 直接提交队列有一个任务就会在线程池中创建一个线程,当任务执行完毕后,由于corePoolSize=0，空闲线程会在60s内被回收
	 * 如果有大量任务被提交,而任务的执行又不那么快时，系统会开启等量的线程处理,这样很快就会耗尽系统资源
	 * 当任务处理时间比较快时,可以使用newCacheThreadPool线程池（怎么感觉这个线程池不受控制，线程数量不受控制）
	 * 
	 * 除了以上
	 * 1.无界任务队列：LinkedBlockingQueue，达到最大线程数量后,不会再创建新线程,未执行完的任务都会放到这个无界队列里
	 * 2.直接提交队列：synchronousQueue,可以设置maximumPoolSize最大值,但一般要设置比较大，当线程达到最大值是会执行拒绝策略,内部使用了无锁
	 * 还有
	 * 3.有界任务队列：ArrayBlockingQueue，因为有界,所以构造函数会带一个容量参数,指定界限。有界队列在未满之前会有限使用corePoolSize线程数来解决问题,如果满了，则会创建线程到maximumPoolSize
	 * 线程数。如果这个时候队列又满了,则执行拒绝策略
	 * 4.优先任务队列：PriorityBlockingQueue可以设置任务的优先级,不用先进先出
	 * 
	 * 队列满了有以下几种拒绝策略,也可以自定义
	 * 1.AbortPolicy:直接抛出异常
	 * 2.CallerRunsPolicy：有点没明白,反正就是不会丢任务,但是性能会急剧下降
	 * 3.DiscardOledestPolicy:丢弃最老的一个任务,也就是排在出口的任务
	 * 4.DiscardPolicy:丢弃多个任务,如果任务允许丢弃,这个是最好的
	 */
}
