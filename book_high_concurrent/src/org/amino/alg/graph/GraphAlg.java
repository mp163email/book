/*
 * Copyright (c) 2008 IBM Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 *
 */
package org.amino.alg.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.amino.ds.graph.AdjacentNode;
import org.amino.ds.graph.DirectedGraph;
import org.amino.ds.graph.DirectedGraphImpl;
import org.amino.ds.graph.Edge;
import org.amino.ds.graph.Graph;
import org.amino.ds.graph.Node;
import org.amino.ds.graph.UndirectedGraph;

/**
 * @author Zhi Gan
 * 
 */
public final class GraphAlg {

	/**
	 * Utility classes should not have a public or default constructor.
	 */
	private GraphAlg() {
	}

	private static final Node DUMMY_NODE = new Node(null);

	/**
	 * Parallel Strong Connected Component algorithm based on [LK
	 * Fleischer2000]. The targeted graph is supposed not to be modified while
	 * computing SCC.
	 * 
	 * some parameters to be set based on experiments result: 1 the initial
	 * capacity of hashset used in BFS visting 2 the initial capacity of hashset
	 * for "remain" set 3 the initial capacity of vector for scc 4 the threshold
	 * of divide-and-conquer
	 * 
	 * @param <E>
	 *            value type of the node in the target graph
	 * @param graph
	 *            directed graph to compute strong components. The graph will
	 *            not be modified
	 * @param threadPool
	 *            the thread pool used to do computation
	 * @return a collection of strong components, each of which in turn is a
	 *         collection of graph nodes
	 */
	public static <E> Collection<Collection<Node<E>>> getStrongComponents(
			DirectedGraph<E> graph, ExecutorService threadPool) {
		StrongComponents<E> scc = new StrongComponents<E>(graph);
		return scc.getStrongComponents();
	}

	/**
	 * parallel Connected Component algorithm based on [Steve Goddard 1996].
	 * Only the initial presentation of the algorithm in the paper, namely A0,
	 * is implemented. A0 is for the CREW-PRAM model, and A1 & A2 are the
	 * adaptation of A0 to mesh-connected computer. Currently, this algorithm
	 * just compute an undirected graph with vertices V={1,2,...,n}, for
	 * simplicity
	 * 
	 * @param <E>
	 *            value type of the node in the target graph
	 * @param graph
	 *            undirected graph to compute connected components. The graph
	 *            will not be modified
	 * @param threadPool
	 *            the thread pool used to do computation
	 * @return a collection of connected components, each of which in turn is a
	 *         collection of graph nodes
	 */
	public static <E> Collection<Collection<Node<E>>> getConnectedComponents(
			UndirectedGraph<E> graph, ExecutorService threadPool) {
		ConnectedComponents<E> cc = new ConnectedComponents<E>(graph,
				threadPool);
		Collection<Collection<Node<E>>> result;
		try {
			result = cc.getConnectedComponents();
		} catch (Exception ex) {
			result = null;
			ex.printStackTrace();
		}
		return result;
	}

	/**
	 * TODO 1 deal with undirected graph with vertices not lableled by interger
	 * number [done] 2 performance comparison with serial version 3 based on
	 * test result, give an direction on how to use this algrothim, including
	 * how large a graph should be, what a desired number of threads be... 4
	 * tree hanging not implemented, due to some obstacle. However, it is said
	 * tree hanging is critical for rapid computing 5 deal with undirected graph
	 * that is being modified
	 * 
	 * <p>
	 * not finished yet. I need to think up a way to collect elements in an
	 * array parallel, and to implement tree hanging
	 */
	private static class ConnectedComponents<E> {
		private static final int INIT_SIZE_FOR_CC = 32;
		private static final int INIT_SIZE_FOR_CCS = 16;
		/**
		 * initialized as a vector, thread-safe.
		 */
		private Collection<Collection<Node<E>>> ccs;
		private UndirectedGraph<E> graph;
		private int numNodes;
		private int average;
		private int updates; // how many updates at one round

		private ArrayList<Node<E>> nodesArray = new ArrayList<Node<E>>();
		private int[] pvalues;
		private ConcurrentHashMap<Node<E>, Integer> map;

		/*
		 * from the implementation of ThreadLocal, frequent get() and set() will
		 * be a performance bottleneck. but i am not sure whether my guess is
		 * reasonable. Any good alternative? private int[] localUpdates; ? it
		 * seems not to be feasible if thread pool considered
		 */
		/*
		 * private ThreadLocal<Integer> localUpdates = new ThreadLocal<Integer>() {
		 * 
		 * @Override protected Integer initialValue() { return new Integer(0); } };
		 */

		private int numThread;
		private ExecutorService pool;

		/**
		 * Constructor.
		 * 
		 * @param graph
		 *            Target directed graph.
		 * @param thread_num
		 *            number of threads to compute strong components if
		 *            thread_num == 1, then it is a sequential execution of this
		 *            algorithm, but not a serial version
		 */
		public ConnectedComponents(UndirectedGraph graph,
				ExecutorService threadPool) {
			this.graph = (UndirectedGraph) graph;
			this.ccs = new Vector<Collection<Node<E>>>(INIT_SIZE_FOR_CCS);
			this.pool = threadPool;
			this.numThread = Runtime.getRuntime().availableProcessors(); // should
			// be
			// better
			// determined

			this.numNodes = graph.size();
			this.average = this.numNodes / numThread;
			this.nodesArray.ensureCapacity(numNodes);
			this.pvalues = new int[numNodes];
			this.map = new ConcurrentHashMap<Node<E>, Integer>(numNodes);

			// make it parallel
			this.nodesArray.addAll(graph.getAllNodes());
		}

		/**
		 * Constructor.
		 * 
		 * @param graph
		 *            Target directed graph.
		 * 
		 * the number of threads to compute components will be determined at
		 * runtime
		 */
		public ConnectedComponents(UndirectedGraph graph) {
			this(graph, Executors.newFixedThreadPool(Runtime.getRuntime()
					.availableProcessors()));
		}

		/**
		 * Compute non-trivial connected components and return them.
		 * 
		 * @return the result collection.
		 * @throws java.lang.Exception
		 */
		public Collection<Collection<Node<E>>> getConnectedComponents()
				throws Exception {
			int i;
			if (graph.size() < 2)
				return this.ccs;
			// inittask1
			Runnable[] tasks = new Runnable[this.numThread];
			for (i = 0; i < this.numThread; i++) {
				final int index = i;
				tasks[i] = new Runnable() {
					public void run() {
						int begin = average * index;
						int end = average * (index + 1);
						if (index == numThread - 1)
							end = numNodes;
						for (int j = begin; j < end; j++) {
							map.put(nodesArray.get(j), j);
							pvalues[j] = j;
						}
					}
				};
			}
			this.runTasks(tasks, 0, numThread);
			// barrier
			// inittask2
			for (i = 0; i < this.numThread; i++) {
				final int index = i;
				tasks[i] = new Runnable() {
					public void run() {
						int begin = average * index;
						int end = average * (index + 1);
						if (index == numThread - 1)
							end = numNodes;
						Iterator<AdjacentNode<E>> itr;
						for (int j = begin; j < end; j++) {
							itr = graph.getLinkedNodes(nodesArray.get(j))
									.iterator();
							while (itr.hasNext()) {
								pvalues[j] = Math.min(pvalues[j], pvalues[map
										.get(itr.next().getNode())]);
							}
						}
					}
				};
			}
			this.runTasks(tasks, 0, this.numThread);
			// barrier

			// main loop
			this.updates = 1; // pretend there is an update
			while (this.updates != 0) {
				this.updates = 0;
				Callable<Integer>[] callables = new Callable[this.numThread];
				for (i = 0; i < this.numThread; i++) {
					final int index = i;
					callables[i] = new Callable<Integer>() {
						public Integer call() {
							int begin = average * index;
							int end = average * (index + 1);
							if (index == numThread - 1)
								end = numNodes;
							int localUpdates = 0;
							Iterator<AdjacentNode<E>> itr;
							// opportunistic pointer jumping
							for (int j = begin; j < end; j++) {
								int minVertexValue = Integer.MAX_VALUE;
								itr = graph.getLinkedNodes(nodesArray.get(j))
										.iterator();
								while (itr.hasNext())
									minVertexValue = Math.min(minVertexValue,
											pvalues[map.get(itr.next()
													.getNode())]);
								if (minVertexValue < pvalues[j]) {
									localUpdates++;
								}
								pvalues[j] = Math.min(pvalues[j],
										minVertexValue);
							}
							return localUpdates;
						}
					};
				}
				this.runTasks2(callables, 0, numThread); // also acts as an
				// implicit barrier
				for (i = 0; i < this.numThread; i++) {
					final int index = i;
					callables[i] = new Callable<Integer>() {
						public Integer call() {
							int begin = average * index;
							int end = average * (index + 1);
							if (index == numThread - 1)
								end = numNodes;
							int localUpdates = 0;
							// normal pointer jumping
							for (int j = begin; j < end; j++) {
								int pvalue = pvalues[j];
								if (pvalues[pvalue] < pvalue) {
									localUpdates++;
								} else if (pvalues[pvalue] > pvalue)
									throw new RuntimeException("fatal error");
								pvalues[j] = pvalues[pvalue];
							}
							return localUpdates;
						}
					};
				}
				this.runTasks2(callables, 0, numThread); // also acts as an
				// implicit barrier
				// System.out.println("updates:" + this.updates);
			}

			this.pool.shutdown();

			return this.ccs;
		}

		// run Runnable tasks. The task to run is from "start" to "end" in the
		// array
		// "tasks"
		private void runTasks(Runnable[] tasks, int start, int end)
				throws Exception {
			Future<Runnable>[] futures = new Future[end - start];
			// long t1 = System.nanoTime();

			for (int i = start; i < end; ++i) {
				FutureTask<Runnable> f = new FutureTask<Runnable>(tasks[i],
						null);
				futures[i - start] = f;
				pool.execute(f);
			}

			for (Future<Runnable> f : futures) {
				try {
					f.get();
				} catch (Exception e) {
					throw e;
				}
			}
		}

		// run Callable tasks. The task to run is from "start" to "end" in the
		// array
		// "tasks"
		private void runTasks2(Callable<Integer>[] tasks, int start, int end)
				throws Exception {
			Future<Integer>[] futures = new Future[end - start];
			// long t1 = System.nanoTime();

			for (int i = start; i < end; ++i) {
				futures[i - start] = pool.submit(tasks[i]);
			}

			for (Future<Integer> f : futures) {
				try {
					this.updates = this.updates + f.get();
				} catch (Exception e) {
					throw e;
				}
			}
		}

		// for performance comparison, compute connected components serially
		public Collection<Collection<Node<E>>> getCCSerially(
				UndirectedGraph<E> graph) {
			// node is one of the elements of nodes, nodes is a subset of dg's
			// nodes
			Queue<Node<E>> q = new LinkedList<Node<E>>();
			Collection<Node<E>> nodes = graph.getAllNodes();
			// hashmap to store the nodes, whose children have been visited
			HashMap<Node<E>, Integer> hashmap = new HashMap<Node<E>, Integer>(
					nodes.size());
			Iterator<Node<E>> itr = nodes.iterator();
			Collection<Node<E>> cc;
			Node<E> temp;
			Node<E> currentRoot;

			while (itr.hasNext()) {
				cc = new Vector<Node<E>>(INIT_SIZE_FOR_CC);
				currentRoot = itr.next();
				if (hashmap.get(currentRoot) != null) // the children already
					// been
					// visited
					continue;
				q.add(currentRoot);
				while (!q.isEmpty()) {
					temp = q.poll(); // temp' children definitely not visited
					cc.add(temp);
					hashmap.put(temp, 1); // temp's childrent been visited now
					Collection<AdjacentNode<E>> collection = graph
							.getLinkedNodes(temp);
					if (collection == null)
						continue;
					Iterator<AdjacentNode<E>> innerIter = collection.iterator();
					while (innerIter.hasNext()) {
						Node<E> toEnqueue = innerIter.next().getNode();
						if (hashmap.get(toEnqueue) == null) // toEnqueue's
							// children
							// not benn visited yet
							q.add(toEnqueue);
					}

				}
				if (cc.size() > 1)
					ccs.add(cc);
				else
					cc = null;
			}
			return ccs;
		}
	}

	/**
	 * ******** end of Connected Components
	 * *************************************************
	 */

	/**
	 * To optimize: 1 transpose a graph in parallel & bfs visit a graph in
	 * parallel 2 test the efficiency of trimming operation from [WC McLendon
	 * III 2001] 3 test the efficiency of [LK Fleischer2003] optimization 4 what
	 * if the targeted graph is being modified? 5 maintain some objects per
	 * thread, such as queue, stack, set, map. so as to reduce frequent creation
	 * and garbage collection 6 test the performance before and after this
	 * optimization
	 * 
	 * 2008/10/04 Mo Jiong Qiu The performance result is really bad, for several
	 * reasons ( sorted by priority): 0 Grid-like graphs are used in the paper's
	 * experiments. And from my understanding, other graphs will not be fit. 1
	 * random graph i used seems not good to be computed in parallel. 2 some
	 * steps of the computation are serial, which is detrimental 3
	 * LinkedBlockingQueue costs much 4 data structure used here such as HashSet
	 * may be not a good choice I am working on these issues
	 */
	private static class StrongComponents<E> {
		private static final int INIT_SIZE_FOR_RETURNS = 32;
		private static final int INIT_SIZE_FOR_WORK_QUEUE = 100;
		private static final int INIT_SIZE_FOR_SCC = 16;
		private static final int TIMEOUT = 100;
		private DirectedGraph<E> dgraph; // the graph to compute
		private Collection<Collection<Node<E>>> sccs; // initialized as a
		// vector,
		// thread-safe
		private transient DirectedGraph<E> transDgraph; // an transposition
		// of
		// dgraph

		static final int THRESHOLD_SERIAL = 1; // threshold of collection size.
		// if lower than this, serial version will be called

		// a Set instance represents a work item
		private BlockingQueue<HashSet<Node<E>>> workQueue = new LinkedBlockingQueue<HashSet<Node<E>>>(
				INIT_SIZE_FOR_WORK_QUEUE);
		private AtomicInteger workCount = new AtomicInteger(0);

		private Thread[] thds;
		private int numWorkers;

		/**
		 * @author ganzhi
		 * 
		 */
		private class Worker implements Runnable {

			public void run() {
				while (true) {
					try {
						HashSet<Node<E>> nodes = workQueue.poll(TIMEOUT,
								TimeUnit.MILLISECONDS);
						if (nodes != null && nodes.size() > 0) {
							dcsc(nodes);
							workCount.decrementAndGet();
							// System.out.println("from worker: wc="+wc+"
							// workQueue.size()="+workQueue.size());
						} else
							// i am notified to stop
							break; // return
					} catch (InterruptedException e) {
						return;
					}
				}
			}
		}

		/**
		 * Constructor.
		 * 
		 * @param dg
		 *            Target directed graph.
		 * 
		 * the number of threads to compute components will be determined at
		 * runtime
		 */
		public StrongComponents(DirectedGraph<E> dg) {
			this(dg, Runtime.getRuntime().availableProcessors());
		}

		/**
		 * Constructor.
		 * 
		 * @param dg
		 *            Target directed graph.
		 * @param numThreads
		 *            number of threads to compute strong components if
		 *            numThreads == 1, then it is a sequential execution of this
		 *            algorithm,
		 */
		public StrongComponents(DirectedGraph<E> dg, int numThreads) {
			this.dgraph = dg;
			this.sccs = new Vector<Collection<Node<E>>>(INIT_SIZE_FOR_SCC);
			this.transDgraph = null;
			if (numThreads < 1)
				numThreads = 1;
			this.numWorkers = numThreads - 1; // main thread is also a worker,
			// but
			// not included here

			if (numWorkers > 0) {
				thds = new Thread[numWorkers];
				for (int i = 0; i < numWorkers; i++) {
					Worker w = new Worker();
					thds[i] = new Thread(w);
					thds[i].start();
				}
			}
		}

		/**
		 * @return non-trival directed graphs, each of which is a collection of
		 *         nodes.
		 */
		public Collection<Collection<Node<E>>> getStrongComponents() {

			if (dgraph == null || dgraph.size() < 2) { // sanity check
				return new Vector<Collection<Node<E>>>(); // return empty
				// collection
			}

			/**
			 * ******* make the following several lines parallel
			 * *****************
			 */
			HashSet<Node<E>> remain; // to represent a work item. all of the
			// nodes here
			Collection<Node<E>> graphNodes = dgraph.getAllNodes();
			Iterator<Node<E>> iter = graphNodes.iterator();
			// get an transposition of dgraph
			this.transDgraph = this.transpose(dgraph);
			remain = new HashSet<Node<E>>(graphNodes.size()); // FIXME: refine
			// me
			while (iter.hasNext())
				remain.add(iter.next());
			/**
			 * ***************************************************************** **
			 */

			// ----------------------------
			try {
				int wc;
				wc = workCount.incrementAndGet(); // we got one work item
				workQueue.put(remain);
				while (wc != 0) { // if work items exists
					HashSet<Node<E>> nodes = workQueue.poll(TIMEOUT,
							TimeUnit.MILLISECONDS);
					if (nodes != null && nodes.size() > 0) {
						dcsc(nodes);
						wc = workCount.decrementAndGet();
					} else
						wc = workCount.get();
					// System.out.println("from main: wc="+wc+"
					// workQueue.size()="+workQueue.size());
				}

				// notify the workers to stop, by putting empty work into the
				// queue
				for (int i = 0; i < this.numWorkers; i++)
					workQueue.put(new HashSet<Node<E>>()); // empty hashset
				// used to
				// notify workers to
				// stop
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
			// ----------------------------

			return this.sccs;
		}

		// divide-and-conquer way to compute strong component
		private void dcsc(HashSet<Node<E>> nodes) {
			// long t0 = System.nanoTime();

			// if size is small enough, compute scc serially
			// if(nodes.size() == 0) return; //we assure nodes.size() > 0 when
			// this
			// function called
			if (nodes.size() < THRESHOLD_SERIAL) {
				this.computeSCCSeriallyTarjan(nodes);
				return;
			}

			// long t1 = System.nanoTime();
			Iterator<Node<E>> nodesIter = nodes.iterator();
			// long t2 = System.nanoTime();

			// select a random vertex
			Node<E> node = nodesIter.next();

			Collection<Node<E>> scc = new Vector<Node<E>>(INIT_SIZE_FOR_SCC);

			// compute PRED & DESC of this "node" in the set of "nodes"
			HashSet<Node<E>> pred = this.getPred(nodes, node);
			HashSet<Node<E>> desc = this.getDesc(nodes, node);
			// long t3 = System.nanoTime();

			// compute PRED\SCC & DESC\SCC & REM
			// choose the smaller one to iterator
			Iterator<Node<E>> iterSmaller; // the iter of the smaller one of
			// PRED &
			// DESC
			Set<Node<E>> bigger; // the bigger one of PRED & DESC
			if (pred.size() > desc.size()) {
				bigger = pred;
				iterSmaller = desc.iterator();
			} else {
				bigger = desc;
				iterSmaller = pred.iterator();
			}
			while (iterSmaller.hasNext()) {
				Node<E> temp = iterSmaller.next();
				nodes.remove(temp); // "nodes" definitely contains "temp"
				if (bigger.contains(temp)) {
					scc.add(temp); // both PRED & DESC contain "temp"
					bigger.remove(temp);
					iterSmaller.remove(); // don't call desc.remove(temp)
					// here?
					// ConcurrentModificationException
				}
			}
			Iterator<Node<E>> iterBigger = bigger.iterator(); // let's
			// iterator
			// the remaining
			// elements in
			// "bigger"
			while (iterBigger.hasNext()) {
				Node<E> temp = iterBigger.next();
				nodes.remove(temp);// "nodes" definitely contains "temp"
			}
			// at this point, PRED\SCC & DESC\SCC are available, also for REM

			if (scc.size() > 1)
				this.sccs.add(scc);

			// long t4 = System.nanoTime();
			// divide-and-conquer here
			try {
				if (pred.size() > 1) { // or > 0, if we want to keep trivial
					// scc
					// System.out.println("pred.size() ="+pred.size());
					workCount.incrementAndGet();
					workQueue.put(pred); // PRED
				}
				if (desc.size() > 1) { // or > 0, if we want to keep trivial
					// scc
					// System.out.println("desc.size() ="+desc.size());
					workCount.incrementAndGet();
					workQueue.put(desc); // DESC
				}
				if (nodes.size() > 1) { // or > 0, if we want to keep trivial
					// scc
					// System.out.println("remain.size() ="+nodes.size());
					workCount.incrementAndGet();
					workQueue.put(nodes); // REM
				}
				// System.out.println("scc.size()="+scc.size());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// compute PRED set of "node" in the set "set"
		private HashSet<Node<E>> getPred(HashSet<Node<E>> set, Node<E> node) {
			return bfs(this.transDgraph, set, node);
		}

		// compute DESC set of "node" in the set "set"
		private HashSet<Node<E>> getDesc(HashSet<Node<E>> set, Node<E> node) {
			return bfs(dgraph, set, node);
		}

		// TODO make it parallel...
		// transpose a graph
		private DirectedGraph<E> transpose(DirectedGraph<E> dg) {
			DirectedGraph<E> dg2 = new DirectedGraphImpl<E>();
			Collection<Node<E>> nodes = dg.getAllNodes();
			Iterator<Node<E>> iter = nodes.iterator();

			while (iter.hasNext()) {
				Node<E> n = iter.next();
				E start = n.getValue();
				Collection<Node<E>> dsts = dg.getDestinations(n);
				Iterator<Node<E>> dstsIter = dsts.iterator();
				while (dstsIter.hasNext()) {
					Node<E> adjNode = dstsIter.next();
					dg2.addEdge(adjNode.getValue(), start, 0);
				}
			}
			return dg2;
		}

		/**
		 * breadth-first-search.
		 * 
		 * @param dg
		 *            Target directed graph.
		 * @param nodes
		 *            only this set of nodes to search in target directed graph.
		 *            to represent a sub-graph
		 * @param node
		 *            the beginning node
		 * @return a set of the nodes reached by BFS-ing "node"
		 */
		private HashSet<Node<E>> bfs(DirectedGraph<E> dg,
				HashSet<Node<E>> nodes, Node<E> node) {
			// long t1 = System.nanoTime();
			// node is one of the elements of nodes, nodes is a subset of dg's
			// nodes
			HashSet<Node<E>> ret = new HashSet<Node<E>>(INIT_SIZE_FOR_RETURNS);
			Queue<Node<E>> q = new LinkedList<Node<E>>();
			Node<E> temp;
			q.add(node);
			while (!q.isEmpty()) {
				temp = q.poll();
				if (ret.add(temp)) { // not existed
					Collection<Node<E>> collection = dg.getDestinations(temp);
					if (collection == null) // no out
						continue;
					Iterator<Node<E>> iter = collection.iterator();
					while (iter.hasNext()) {
						Node<E> toEnqueue = iter.next();
						if (nodes.contains(toEnqueue))
							q.add(toEnqueue);
					}
				}
				// else {
				// // existed, just ignore it
				// }
			}
			q = null;
			// long t2 = System.nanoTime();
			// this.bfs_time.addAndGet((int)(t2-t1));
			return ret;
		}

		/** *********** tarjan algorithm of scc computing. ************** */

		private class WrappedNode {
			public static final int WHITE = 0;
			public static final int GRAY = 1;
			public static final int BLACK = 2;
			public Node<E> node; // which node
			public int color; // color
			public int dfs; // depth index
			public int lowlink; // lowlink <= dfs
			public static final int UNVISITED = 0; // tarjan-visit
			public static final int VISITEDANDNOTATSTACK = 2;
			public static final int ATSTACK = 1;
			public int state;
		}

		private int min(int a, int b) {
			return a < b ? a : b;
		}

		private void computeSCCSeriallyTarjan(Set<Node<E>> nodes) {
			Map<Node<E>, WrappedNode> map = new HashMap<Node<E>, WrappedNode>(
					THRESHOLD_SERIAL);
			Iterator<Node<E>> iter = nodes.iterator();
			// int i = 0;
			Collection<Node<E>> scc;

			int maxDfs = 0;
			// Collection<WrappedNode_T<E>> U = map.values();//needless, use
			// WrappedNode's color field
			Stack<WrappedNode> stack = new Stack<WrappedNode>();
			Stack<WrappedNode> stackTarjan = new Stack<WrappedNode>();

			// initialization
			while (iter.hasNext()) {
				Node<E> node = iter.next();
				WrappedNode wn = new WrappedNode();
				wn.node = node;
				map.put(node, wn);
			}

			iter = nodes.iterator();

			// DFS(g)
			while (iter.hasNext()) {
				Node<E> node = iter.next();
				WrappedNode wnode = map.get(node);
				if (wnode.color == WrappedNode.BLACK) {
					continue;
				}
				if (wnode.color == WrappedNode.GRAY) {
					// impossible, if a node has been discoved, it must be
					// visited
					// at this point
					throw new RuntimeException("fatal error 1");
				}
				// dfs-visit(node)
				stack.push(wnode);
				while (!stack.isEmpty()) {
					WrappedNode temp = stack.pop();
					if (temp.color == WrappedNode.BLACK) {
						// visited node, here it is just a duplicate reference
						continue;
					}
					if (temp.color == WrappedNode.GRAY) {
						// now all the descentants of temp has been visited
						temp.color = WrappedNode.BLACK;
						Collection<Node<E>> c = dgraph
								.getDestinations(temp.node);
						if (c != null && c.size() > 0) {
							Iterator<Node<E>> innerIter = c.iterator();
							while (innerIter.hasNext()) {
								Node<E> temp2 = innerIter.next();
								WrappedNode innerWnode = map.get(temp2);
								if (innerWnode == null)
									continue;
								// tarjan visit node here.

								// tree edge
								if (temp.state == WrappedNode.UNVISITED)
									temp.lowlink = min(temp.lowlink,
											innerWnode.lowlink);
								// cross edge that go between vertices in the
								// same
								// dfs tree
								else if (innerWnode.state == WrappedNode.ATSTACK)
									temp.lowlink = min(temp.lowlink,
											innerWnode.lowlink);
								// cross edge that go between vertices in
								// different
								// dfs tree
								// ---------------
							}
						}

						if (temp.lowlink == temp.dfs) {
							scc = new Vector<Node<E>>();
							WrappedNode temp3;
							do {
								temp3 = stackTarjan.pop();
								scc.add(temp3.node);
								temp3.state = WrappedNode.VISITEDANDNOTATSTACK;
							} while (temp3 != temp);
							if (scc.size() > 1)
								this.sccs.add(scc);
						}

						continue;
					}

					temp.color = WrappedNode.GRAY; // discoved just now
					stack.push(temp);

					// tree edge
					temp.dfs = maxDfs;
					temp.lowlink = maxDfs;
					maxDfs = maxDfs + 1;
					stackTarjan.push(temp);
					temp.state = WrappedNode.ATSTACK;

					Collection<Node<E>> c = dgraph.getDestinations(temp.node);
					if (c == null || c.size() == 0) {
						continue;
					}
					Iterator<Node<E>> innerIter = c.iterator();
					while (innerIter.hasNext()) {
						Node<E> toPush = innerIter.next();
						WrappedNode innerWnode = map.get(toPush);
						if (innerWnode != null
								&& innerWnode.color == WrappedNode.WHITE)
							stack.push(innerWnode);
					}
				}// while(!stack.isEmpty())
			}// while(iter.hasNext())
		}
	}

	/*
	 * end of Strong Components
	 */

	/**
	 * parallel MST algorithm based on Boruvka's algorithm.
	 * 
	 * @param <E>
	 *            value type of the node in the target graph
	 * @param graph
	 *            the graph to compute minimum spanning tree
	 * @param pool
	 *            external thread pool
	 * @return a minimum spanning tree in the form of undirected graph
	 * @throws InterruptedException
	 *             thrown exception if be interrupted
	 * @throws ExecutionException
	 *             Exception thrown when attempting to retrieve the result of a
	 *             task that aborted by throwing an exception
	 */
	public static <E> Graph<E> getMST(final UndirectedGraph graph,
			ExecutorService pool) throws InterruptedException,
			ExecutionException {
		if (!(graph instanceof UndirectedGraph)) {
			throw new IllegalArgumentException(
					"graph is not an undirected graph");
		}

		final Graph<E> mst = new UndirectedGraph<E>();
		Collection<Node<E>> nodes = graph.getAllNodes();
		final Collection<Node<E>> graphNodes = graph.getAllNodes();

		int size = nodes.size();
		Runnable[] tasks = new Runnable[size];

		int index = 0;

		final ConcurrentHashMap<Node<E>, Collection<Edge<E>>> edges = new ConcurrentHashMap<Node<E>, Collection<Edge<E>>>();

		// step 0: init , get all the edge list and store them in map edges and
		// add all node into mst
		Iterator<Node<E>> it = nodes.iterator();
		while (it.hasNext()) {
			final Node<E> n = it.next();
			tasks[index++] = new Runnable() {
				public void run() {
					Collection<Edge<E>> edge = graph.getLinkedEdges(n);
					edges.put(n, edge);
					mst.addNode(n.getValue());
				}
			};
		}
		runTasks(tasks, 0, index, pool);

		final AtomicBoolean finish = new AtomicBoolean(false);
		final ConcurrentHashMap<Node<E>, Node<E>> roots = new ConcurrentHashMap<Node<E>, Node<E>>();

		while ((size = (nodes = edges.keySet()).size()) > 1) {
			// System.out.println("222 " + size);
			Iterator<Node<E>> iter = nodes.iterator();
			index = 0;
			// final CountDownLatch wait = new CountDownLatch(size);

			while (iter.hasNext()) {
				final Node<E> cur = iter.next();

				// step 1:find lightest edge to each vertex , add it to
				// the new graph and remove it
				tasks[index++] = new Runnable() {
					public void run() {
						Edge<E> e = getMinimumEdgeWith(edges, cur);
						if (e == null) {
							finish.set(true);
							return;
						}
						Node<E> end = e.getEnd();
						Node<E> start = e.getStart();

						Node<E> n = roots.get(end);
						Node<E> c = roots.get(start);
						if (n == null)
							n = end;
						if (c == null)
							c = start;

						// merge the two trees. we vote the node with small
						// compare as root
						int cmp = n.compareTo(c);
						// if (cmp > 0) {
						// Node<E> v = roots.putIfAbsent(n, c);
						// } else if (cmp < 0) {
						// Node<E> v = roots.putIfAbsent(c, n);
						//						}

						if (cmp != 0)
							mst.addEdge(start, end, e.getWeight());
					}
				};
			}
			runTasks(tasks, 0, index, pool);
			if (finish.get())
				throw new IllegalArgumentException(
						"graph is not a connected graph");

			// step 2: find parent
			Enumeration<Node<E>> enu = roots.keys();
			index = 0;
			while (enu.hasMoreElements()) {
				// System.out.println("444");
				final Node<E> cur = enu.nextElement();
				tasks[index++] = new Runnable() {
					public void run() {
						Node<E> p = cur;
						Node<E> tmp = cur;
						do {
							p = tmp;
							tmp = roots.get(tmp);
						} while (tmp != null); // TODO GZ:sometime infinite
						// loop here

						if (cur != p) {
							roots.put(cur, p);
						}
					}
				};
			}
			runTasks(tasks, 0, index, pool);

			// step 3: rename vertex in original graph and remove the edges;
			index = 0;

			iter = graphNodes.iterator();

			while (iter.hasNext()) {
				// System.out.println("555");
				final Node<E> cur = iter.next();

				tasks[index++] = new Runnable() {
					public void run() {
						Node<E> p = roots.get(cur);
						// for every parent, add all children's edgelist and
						// remove all the inner edge
						if (p == null) {
							Collection<Edge<E>> coll = edges.get(cur);
							HashSet<Node<E>> children = new HashSet<Node<E>>();

							Iterator<Node<E>> it = graphNodes.iterator();
							while (it.hasNext()) {
								Node<E> n = it.next();
								if (roots.get(n) == cur) {
									children.add(n);
									if (edges.containsKey(n)) {
										coll.addAll(edges.get(n));
										edges.remove(n);
									}
								}
							}

							Iterator<Edge<E>> iterator = coll.iterator();
							while (iterator.hasNext()) {
								Edge<E> e = iterator.next();
								Node<E> end = e.getEnd();
								if (end.equals(cur) || children.contains(end)) {
									iterator.remove();
								}

							}
						}
					}
				};
			}
			runTasks(tasks, 0, index, pool);
		}

		// remove loop edge in MST
		nodes = mst.getAllNodes();
		index = 0;
		Iterator<Node<E>> iter = nodes.iterator();
		while (iter.hasNext()) {
			final Node<E> cur = iter.next();
			tasks[index++] = new Runnable() {
				public void run() {
					// use a hashset to find the same node, pay space for time
					HashSet<Node<E>> sets = new HashSet<Node<E>>();
					Collection<AdjacentNode<E>> lns = mst.getLinkedNodes(cur);
					Iterator<AdjacentNode<E>> iterAdj = lns.iterator();

					AdjacentNode<E> adj;
					while (iterAdj.hasNext()) {
						adj = iterAdj.next();
						Node<E> tar = adj.getNode();
						if (!sets.add(tar)) {
							iterAdj.remove();
						}
					}
				}
			};
		}
		runTasks(tasks, 0, index, pool);
		return mst;
	}

	private static <E> Edge<E> getMinimumEdgeWith(
			ConcurrentHashMap<Node<E>, Collection<Edge<E>>> map, Node<E> node) {
		Collection<Edge<E>> edge = map.get(node);
		double w = Double.MAX_VALUE;
		Edge<E> e = null;
		Edge<E> t = null;
		Iterator<Edge<E>> iter = edge.iterator();
		while (iter.hasNext()) {
			e = iter.next();
			double weight = e.getWeight();
			if (weight < w) {
				t = e;
				w = weight;
			}
		}
		return t;
	}

	private static void runTasks(Runnable[] tasks, int start, int end,
			ExecutorService pool) throws InterruptedException,
			ExecutionException {
		Future<Runnable>[] futures = new Future[end - start];

		for (int i = start; i < end; ++i) {
			FutureTask<Runnable> f = new FutureTask<Runnable>(tasks[i], null);
			futures[i - start] = f;
			pool.execute(f);
		}

		for (Future<Runnable> f : futures) {
			f.get();
		}
	}

	/*
	 * start of shortest path
	 */
	// nodeComparator compare the weight of node
	private static Comparator<AdjacentNode> nodeComparator = new Comparator<AdjacentNode>() {
		public int compare(AdjacentNode n1, AdjacentNode n2) {
			double weightN1 = n1.getWeight();
			double weightN2 = n2.getWeight();

			return (weightN1 < weightN2 ? -1 : (weightN1 == weightN2 ? 0 : 1));
		}
	};

	/**
	 * This is an implementation of a parallelization of Dijkstra's shortest
	 * path algorithm described by Crauser, Mehlhorn, Meyer and Sanders in their
	 * paper "A Parallelization of Dijkstra's Shortest Path Algorithm" in 23rd
	 * Symposium on Mathematical Foundations of Computer Science, 1998. To gain
	 * a complete understanding of this data structure, please first read this
	 * paper, available at:
	 * http://citeseer.ist.psu.edu/crauser98parallelization.html
	 * 
	 * This paper propose simple criteria which divide Dijkstra's sequential
	 * SSSP algorithm into a number of phases,such that the operations within a
	 * phase can be done in parallel.
	 * 
	 * In the first variant (OUT-version) we compute a threshold defined via the
	 * weights of the outgoing edges:let L=min{tent(u) + c(u,z) : u is queued
	 * and (u,z) belongs E} and remove all nodes v from the queue then dist(v) =
	 * tent(v). The threshold for the OUT-criterion can either be computed via a
	 * second priority queue for o(v) = tent(v) + min(c(u,v) : (u,v) belongs to
	 * E} or even on the fly while while removing nodes.
	 * 
	 * The second variant, the IN-version, is defined via the incoming edges:
	 * let M = min{tent(u) : u is queued} and i(v) = tent(v) - min{c(u,v) :
	 * (u,v) belongs to E} for any queued vertex v. Then v can be safely removed
	 * from the queue if i(v) <= M. Removable nodes of the IN-type can be found
	 * efficiently by using an additional priority queue for i(.).
	 * 
	 * Finally, the INOUT-applies both criteria in conjunction.
	 * 
	 * Sample performance results here.
	 * 
	 * @param <E>
	 *            type of element on node
	 * @param graph
	 *            Graph
	 * @param exec
	 *            Thread pool for executing task in parallel.
	 * @param source
	 *            source node
	 * @param end
	 *            end node
	 * @return the length of shortest path from source node to end node
	 */
	public static <E> double getShortestPath(Graph<E> graph,
			ExecutorService exec, E source, E end) {
		// if source is the same as end, return 0
		if (source.equals(end))
			return 0;

		return getShortestPath(graph, exec, new Node<E>(source), new Node<E>(
				end));
	}

	/**
	 * This is an implementation of a parallelization of Dijkstra's shortest
	 * path algorithm described by Crauser, Mehlhorn, Meyer and Sanders in their
	 * paper "A Parallelization of Dijkstra's Shortest Path Algorithm" in 23rd
	 * Symposium on Mathematical Foundations of Computer Science, 1998. To gain
	 * a complete understanding of this data structure, please first read this
	 * paper, available at:
	 * http://citeseer.ist.psu.edu/crauser98parallelization.html
	 * 
	 * This paper propose simple criteria which divide Dijkstra's sequential
	 * SSSP algorithm into a number of phases,such that the operations within a
	 * phase can be done in parallel.
	 * 
	 * In the first variant (OUT-version) we compute a threshold defined via the
	 * weights of the outgoing edges:let L=min{tent(u) + c(u,z) : u is queued
	 * and (u,z) belongs E} and remove all nodes v from the queue then dist(v) =
	 * tent(v). The threshold for the OUT-criterion can either be computed via a
	 * second priority queue for o(v) = tent(v) + min(c(u,v) : (u,v) belongs to
	 * E} or even on the fly while while removing nodes.
	 * 
	 * The second variant, the IN-version, is defined via the incoming edges:
	 * let M = min{tent(u) : u is queued} and i(v) = tent(v) - min{c(u,v) :
	 * (u,v) belongs to E} for any queued vertex v. Then v can be safely removed
	 * from the queue if i(v) <= M. Removable nodes of the IN-type can be found
	 * efficiently by using an additional priority queue for i(.).
	 * 
	 * Finally, the INOUT-applies both criteria in conjunction.
	 * 
	 * Sample performance results here.
	 * 
	 * @param <E>
	 *            type of element on node
	 * @param graph
	 *            Graph
	 * @param exec
	 *            Thread pool for executing task in parallel.
	 * @param source
	 *            source node
	 * @param end
	 *            end node
	 * 
	 * @return the length of shortest path from source node to end node
	 */
	public static <E> double getShortestPath(Graph<E> graph,
			ExecutorService exec, Node<E> source, Node<E> end) {
		// if source is the same as end, return 0
		if (end.equals(source)) {
			return 0;
		}

		// executor for processing node over threshold
		final CompletionService<Boolean> completionService = new ExecutorCompletionService<Boolean>(
				exec);
		// size of nodes in the graph
		final int nodeSize = graph.getAllNodes().size();

		// min weight on the edges in the graph. precompute once and for all
		// upon initialization
		final Map<Node<E>, Double> minWeight = computeMinWeight(graph);

		// tentative length of path from source node to end nodes
		final Map<Node<E>, Double> tentative = computeTentative(graph, source);
		// auxiliary array to record predecessor of shortest path. use to
		// recover the shortest path
		final Map<Node<E>, AdjacentNode<E>> predecessor = computePredecessor(
				graph, source);
		// nodes queue waiting for being processed
		final PriorityQueue<AdjacentNode<E>> queued = new PriorityQueue<AdjacentNode<E>>(
				nodeSize, nodeComparator);
		// enqueue the source node as initiation
		queued.offer(new AdjacentNode<E>(source, (tentative.get(source))));

		/*
		 * thresholds decide which nodes should be choose to be processed
		 * parallely
		 */
		final PriorityQueue<AdjacentNode<E>> thresholds = new PriorityQueue<AdjacentNode<E>>(
				nodeSize, nodeComparator);
		// enqueue the source node as initiation
		thresholds.offer(new AdjacentNode<E>(source,
				(tentative.get(source) + minWeight.get(source))));
		// min threshold node from threshold queue
		AdjacentNode<E> thrsNode;
		// value of min threshold
		double threshold;
		// settled list all nodes has be processed
		final List<Node<E>> settled = new ArrayList<Node<E>>();

		while (!queued.isEmpty()) {
			// there is still nodes to be processed

			// find the first node not be settled from thresholds
			do {
				thrsNode = thresholds.poll();
			} while (settled.contains(thrsNode.getNode()));

			// get value of threshold
			threshold = thrsNode.getWeight();

			while (!queued.isEmpty()) {
				// there is still nodes to be processed
				if (queued.peek().getWeight() <= threshold) {
					// weight of the first unprocessed node is less than
					// threshold. it should be dequed for processing.
					final Node<E> v = queued.poll().getNode();
					// mark it as processed
					settled.add(v);

					// get the size of linked nodes
					int size = graph.getLinkedNodes(v).size();

					for (AdjacentNode<E> wAdj : graph.getLinkedNodes(v)) {
						// process linked node
						final Node<E> w = wAdj.getNode();
						final double weight = wAdj.getWeight();

						// submit the linked node to executor to parallelly
						// process
						completionService.submit(new Callable<Boolean>() {
							public Boolean call() {
								double x = tentative.get(v) + weight;
								// find a shorter path from source node v to
								// end node w
								if (x < tentative.get(w)) {

									// update tentative array and predecessor
									// array
									tentative.put(w, x);
									predecessor.put(w, new AdjacentNode<E>(v,
											weight));

									// update the priority queue queued and
									// threshold to reflect the new shorter path
									decreasKey(queued,
											new AdjacentNode<E>(w, x));
									decreasKey(thresholds, new AdjacentNode<E>(
											w, x + minWeight.get(w)));
								}
								return true;
							}
						});
					}

					// synchronization bar.continue after all linked node has
					// been processed
					try {
						for (int i = 0; i < size; i++) {
							completionService.take();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					break;
				}
			}
		}

		// recover path using predecessor array
		double length = 0;
		boolean reachable = false;
		AdjacentNode<E> start;
		while ((start = predecessor.get(end)).getNode() != DUMMY_NODE) {
			reachable = true;
			length += start.getWeight();
			end = start.getNode();
		}

		// return the length if reachable, otherwise return infinity
		if (reachable) {
			return length;
		} else {
			return Double.POSITIVE_INFINITY;
		}
	}

	/**
	 * update the heap when node in the heap changed.
	 * 
	 * @param <E>
	 *            type of element in the node
	 * @param heap
	 *            heap
	 * @param node
	 *            node in the heap whose value has changed
	 */
	private static synchronized <E> void decreasKey(
			PriorityQueue<AdjacentNode<E>> heap, AdjacentNode<E> node) {
		// iterator of heap
		Iterator<AdjacentNode<E>> iter = heap.iterator();
		AdjacentNode<E> temp;

		// for every node in the heap
		while (iter.hasNext()) {
			temp = iter.next();

			// find the target node
			if (temp.getNode().equals(node.getNode())
					&& temp.getWeight() > node.getWeight()) {
				// remove it from heap
				iter.remove();
				// reinsert it into heap
				heap.offer(node);
				return;
			}
		}
		// insert node into heap if not found
		heap.offer(node);
	}

	/**
	 * Initial tentative array. a tentative array which store the internal value
	 * of final shortest path array
	 * 
	 * @param <E>
	 *            type of element in the node
	 * @param graph
	 *            graph
	 * @param source
	 *            source node
	 * @return initial a tentative array which store the internal value of final
	 *         shortest path array
	 */
	private static <E> Map<Node<E>, Double> computeTentative(Graph<E> graph,
			Node<E> source) {
		// tentative array return
		Map<Node<E>, Double> tentative = new ConcurrentHashMap<Node<E>, Double>();

		// put the initial value
		for (Node<E> node : graph.getAllNodes()) {
			tentative.put(node, Double.POSITIVE_INFINITY);
		}

		// set length of source - source zero
		tentative.put(source, 0.0);
		return tentative;
	}

	/**
	 * Initial predecessor array. A predecessor array stores the information to
	 * recover shortest path from tentative array.
	 * 
	 * @param <E>
	 *            type of element in the node
	 * @param graph
	 *            graph
	 * @param source
	 *            source node
	 * @return initial a predecessor array stores the information to recover
	 *         shortest path from tentative array.
	 */
	@SuppressWarnings("unchecked")
	private static <E> ConcurrentHashMap<Node<E>, AdjacentNode<E>> computePredecessor(
			Graph<E> graph, Node<E> source) {
		// predecessor array stores the information to recover shortest path
		// from tentative array
		ConcurrentHashMap<Node<E>, AdjacentNode<E>> predecessor = new ConcurrentHashMap<Node<E>, AdjacentNode<E>>();

		// put the initial value
		for (Node<E> node : graph.getAllNodes()) {
			predecessor.put(node, new AdjacentNode<E>(DUMMY_NODE, 0.0));
		}

		return predecessor;
	}

	/**
	 * Computer minimal weight for every node. read-only array.
	 * 
	 * @param <E>
	 *            type of element in node
	 * @param graph
	 *            graph
	 * @return minimal weight array for every node.
	 */
	private static <E> Map<Node<E>, Double> computeMinWeight(Graph<E> graph) {
		// minWeight record the minimal weight for every node
		Map<Node<E>, Double> minWeight = new ConcurrentHashMap<Node<E>, Double>();

		// linked nodes of target node
		Collection<AdjacentNode<E>> linkedNodes;
		for (Node<E> node : graph.getAllNodes()) {
			// get linked nodes of target node
			linkedNodes = graph.getLinkedNodes(node);
			// recored the min weight of target node if exist, otherwise put
			// infinity
			if (!linkedNodes.isEmpty()) {
				minWeight.put(node, Collections
						.min(linkedNodes, nodeComparator).getWeight());
			} else {
				minWeight.put(node, Double.POSITIVE_INFINITY);
			}
		}
		return minWeight;
	}

	/*
	 * end of shortest path
	 */
}
