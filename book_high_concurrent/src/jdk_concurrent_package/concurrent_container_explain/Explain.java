package jdk_concurrent_package.concurrent_container_explain;

/**
 * Jdk并发容器-超好用工具类（数据结构）
 * @author mp
 * @date 2016-7-14 上午9:47:46
 */
public class Explain {

	/**
	 *1.ConcurrentHashMap:高效并发的线程安全的无序的map,使用了分段锁(16个段)，多线程环境下读不加锁，写只对特定的段加锁,但求总数size的时候要获取所有段的锁,会慢。使用减少锁粒度进行了优化
	 *		Collections.synchronizedMap()也会生成一个线程安全的hashmap,他使用委托,内部封装了一个map,外部所有map操作都使用内部封装的这个map,而自己只负责保证线程安全,就是在所有map的操作上加锁
	 *      	这种同步map,在并发级别不高的情况下可以用,但在高并发环境下,用上者。
	 *
	 *
	 *2.ConcurrentSkipListMap:是一个线程安全的按Key排序的map,使用跳表数据结构,无锁读取,本质是同时维护了多个链表,并且链表是分层的. 在多线程环境下(>=10个线程),要想得到一个默认按Key排序的Map就用它
	 *			2-1: 跳表是线程安全的,而且读是无锁的。他是分层查找,用的是以空间换时间的思想
	 *			2-1：跳表的输出和HashMap不同,是按Key排序的,整数时排序正确,字符串排序不正确
	 *			2-2：多线程环境下,线程多少对耗时影响不大,而具有相同功能的Collections.synconizedSortMap(TreeMap)线程越多,耗时越长
	 *
	 *
	 *3.CopyOnWriteArrayList:高效读，读是无锁的,在读多写少的场合,性能比Vector好,关键在于CopyOnWrite,只有写写互斥,拷贝一个副本,修改副本,用新数组替换老数组
	 *		Collections.synchronizedList(new LinkedList),可以生成一个线程安全的LinkedList
	 *
	 *
	 *4.ConcurrentLinkedQueue:高效并发的线程安全的LinkedList,应该算是高并发环境中性能最好的队列,无锁，因为其内部是基于无锁的CAS[比较交换,核心是一个闭环的for循环,直至尝试成功]实现
	 *
	 * 
	 *5.BlockingQueue:是一个接口,阻塞队列,非常适用于作为数据共享的通道,有两个常用实现类,内部使用wait-notify实现
	 *		5-1:ArrayBlockingQueue,内部数组实现,适合做有界队列或边界值比较小的队列,需指定可容纳的最大个数(毕竟数组的动态扩展不方便),如果初始值很大,会一口气吃掉很多内存.
	 *		5-2:LinkedBlockingQueue,内部链表实现,适合做无界队列或边界值非常大的队列,使用锁分离对其进行了优化
	 * 
	 */
	
}
