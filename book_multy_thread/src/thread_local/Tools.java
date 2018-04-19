package thread_local;

public class Tools {
	public static ThreadLocal<Integer> t1 = new ThreadLocal<>();//这是一个变量,验证的是变量的隔离性(虽在表面上看是一个变量，但对于多线程来说，你是你的值，我是我的值)，既然是变量，就只能放一个值       感觉用处不大,也可能是理解不到位
}
