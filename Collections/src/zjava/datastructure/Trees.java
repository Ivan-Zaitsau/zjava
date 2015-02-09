package zjava.datastructure;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Stack;

/**
 * This class contains useful methods to work with trees (data structures) and tree-nodes
 * 
 * @author Ivan Zaitsau
 */
final public class Trees {
	
	/**
	 * <tt>Iterator</tt> which navigates root node and it's successors
	 * in depth-first search order.<br>
	 * Does not support remove operation.
	 */
	private static class DfsIterator implements Iterator<Node>, Iterable<Node> {
		
		private final Stack<Iterator<Node>> iteratorsStack;

		private DfsIterator(Node root) {
			iteratorsStack = new Stack<>();
			iteratorsStack.push(Collections.singletonList(root).iterator());
		}
		
		public boolean hasNext() {
			return !iteratorsStack.empty();
		}

		public Node next() {
			if (iteratorsStack.empty())
				throw new NoSuchElementException();
			Node currentNode = iteratorsStack.peek().next();
			iteratorsStack.push(currentNode.getChildren().iterator());
			while (!iteratorsStack.empty() && !iteratorsStack.peek().hasNext())
				iteratorsStack.pop();
			return currentNode;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
		
		public Iterator<Node> iterator() {
			return this;
		}
	}
	
	/**
	 * Returns <tt>Iterable</tt> which navigates root node and it's successors
	 * in depth-first search order.
	 * 
	 * @return <tt>Iterable</tt> which navigates root node and it's successors
	 * in depth-first search order.
	 */
	public static Iterable<Node> depthFirstSearch(Node root) {
		return new DfsIterator(root);
	}
	
	/**
	 * <tt>Iterator</tt> which navigates root node and it's successors
	 * in breadth-first search order.<br>
	 * Does not support remove operation.
	 */
	private static class BfsIterator implements Iterator<Node>, Iterable<Node> {

		private Iterator<Node> currentIterator;
		private final Queue<List<Node>> nodesQueue = new LinkedList<>();

		private BfsIterator(Node root) {
			currentIterator = Collections.singletonList(root).iterator();
		}
		
		public boolean hasNext() {
			return currentIterator.hasNext();
		}

		public Node next() {
			if (!currentIterator.hasNext())
				throw new NoSuchElementException();
			Node currentNode = currentIterator.next();
			nodesQueue.add(currentNode.getChildren());
			while (!currentIterator.hasNext() && !nodesQueue.isEmpty())
				currentIterator = nodesQueue.poll().iterator();
			return currentNode;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
		
		public Iterator<Node> iterator() {
			return this;
		}
	}
	
	/**
	 * Returns <tt>Iterable</tt> which navigates root node and it's successors
	 * in breadth-first search order.
	 * 
	 * @return <tt>Iterable</tt> which navigates root node and it's successors
	 * in breadth-first search order.
	 */
	public static Iterable<Node> breadthFirstSearch(Node root) {
		return new BfsIterator(root);
	}
	
	private Trees() {};
}
