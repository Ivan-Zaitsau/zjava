package extension.tree.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import extension.tree.Node;

public class NodeTest {
	
	@SuppressWarnings("unchecked")
	private <E> E[] toArray(Iterable<Node<E>> iterable) {
		List<E> values = new ArrayList<>();
		for (Node<E> node : iterable)
			values.add(node.getValue());
		return (E[]) values.toArray();
	}
	
	@Test(timeout = 200)
	public void testDfs01() {
		Node<Integer> node01 = new Node<>(1);
		assertArrayEquals(new Integer[] {1}, toArray(node01.depthFirstSearch()));
		
		Node<Integer>
			node02 = Node.createNode(node01, 2),
			node03 = Node.createNode(node01, 3),
			node04 = Node.createNode(node01, 4),
			node05 = Node.createNode(node02, 5),
			node06 = Node.createNode(node02, 6),
			node07 = Node.createNode(node03, 7),
			node08 = Node.createNode(node04, 8),
			node09 = Node.createNode(node04, 9),
			node10 = Node.createNode(node04, 10),
			node11 = Node.createNode(node05, 11);
		
		assertArrayEquals(new Integer[] {1, 2, 5, 11, 6, 3, 7, 4, 8, 9, 10}, toArray(node01.depthFirstSearch()));
		assertArrayEquals(new Integer[] {2, 5, 11, 6}, toArray(node02.depthFirstSearch()));
		assertArrayEquals(new Integer[] {3, 7}, toArray(node03.depthFirstSearch()));
		assertArrayEquals(new Integer[] {4, 8, 9, 10}, toArray(node04.depthFirstSearch()));
		assertArrayEquals(new Integer[] {5, 11}, toArray(node05.depthFirstSearch()));
		assertArrayEquals(new Integer[] {6}, toArray(node06.depthFirstSearch()));
		assertArrayEquals(new Integer[] {7}, toArray(node07.depthFirstSearch()));
		assertArrayEquals(new Integer[] {8}, toArray(node08.depthFirstSearch()));
		assertArrayEquals(new Integer[] {9}, toArray(node09.depthFirstSearch()));
		assertArrayEquals(new Integer[] {10}, toArray(node10.depthFirstSearch()));
		assertArrayEquals(new Integer[] {11}, toArray(node11.depthFirstSearch()));
	}
	
	@Test(timeout = 200)
	public void testBfs01() {
		Node<Integer> node01 = new Node<>(1);
		assertArrayEquals(new Integer[] {1}, toArray(node01.breadthFirstSearch()));

		Node<Integer>
			node02 = Node.createNode(node01, 2),
			node03 = Node.createNode(node01, 3),
			node04 = Node.createNode(node01, 4),
			node05 = Node.createNode(node02, 5),
			node06 = Node.createNode(node02, 6),
			node07 = Node.createNode(node03, 7),
			node08 = Node.createNode(node04, 8),
			node09 = Node.createNode(node04, 9),
			node10 = Node.createNode(node04, 10),
			node11 = Node.createNode(node05, 11);
		
		assertArrayEquals(new Integer[] {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11}, toArray(node01.breadthFirstSearch()));
		assertArrayEquals(new Integer[] {2, 5, 6, 11}, toArray(node02.breadthFirstSearch()));
		assertArrayEquals(new Integer[] {3, 7}, toArray(node03.breadthFirstSearch()));
		assertArrayEquals(new Integer[] {4, 8, 9, 10}, toArray(node04.breadthFirstSearch()));
		assertArrayEquals(new Integer[] {5, 11}, toArray(node05.breadthFirstSearch()));
		assertArrayEquals(new Integer[] {6}, toArray(node06.breadthFirstSearch()));
		assertArrayEquals(new Integer[] {7}, toArray(node07.breadthFirstSearch()));
		assertArrayEquals(new Integer[] {8}, toArray(node08.breadthFirstSearch()));
		assertArrayEquals(new Integer[] {9}, toArray(node09.breadthFirstSearch()));
		assertArrayEquals(new Integer[] {10}, toArray(node10.breadthFirstSearch()));
		assertArrayEquals(new Integer[] {11}, toArray(node11.breadthFirstSearch()));
	}
}
