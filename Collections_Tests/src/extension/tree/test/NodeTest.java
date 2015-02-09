package extension.tree.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Test;

import extension.tree.Node;
import extension.tree.SimpleNode;

public class NodeTest {
	
	private Object[] toArray(Iterable<Node> iterable) {
		List<Object> values = new ArrayList<>();
		for (Node node : iterable)
			values.add(((SimpleNode<?>) node).getValue());
		return values.toArray();
	}

	// - edge cases
	
	@SuppressWarnings("unchecked")
	@Test(timeout = 200)
	public void iteratorAtEnd01() {
		SimpleNode<Integer> node01 = new SimpleNode<>(1);
		Iterator<Node> bfsIter = node01.breadthFirstSearch().iterator();
		assertEquals(Integer.valueOf(1), ((SimpleNode<Integer>) bfsIter.next()).getValue());
		boolean noSuchElement = false;
		try {
			bfsIter.next();
		}
		catch(NoSuchElementException nsee) {
			noSuchElement = true;
		}
		assertTrue(noSuchElement);
		Iterator<Node> dfsIter = node01.depthFirstSearch().iterator();
		assertEquals(Integer.valueOf(1), ((SimpleNode<Integer>) dfsIter.next()).getValue());
		noSuchElement = false;
		try {
			dfsIter.next();
		}
		catch(NoSuchElementException nsee) {
			noSuchElement = true;
		}
		assertTrue(noSuchElement);
	}
	
	// - basic tests
	@Test(timeout = 200)
	public void testDfs01() {
		SimpleNode<Integer> node01 = new SimpleNode<>(1);
		assertArrayEquals(new Integer[] {1}, toArray(node01.depthFirstSearch()));
		
		SimpleNode<Integer>
			node02 = SimpleNode.createNode(node01, 2),
			node03 = SimpleNode.createNode(node01, 3),
			node04 = SimpleNode.createNode(node01, 4),
			node05 = SimpleNode.createNode(node02, 5),
			node06 = SimpleNode.createNode(node02, 6),
			node07 = SimpleNode.createNode(node03, 7),
			node08 = SimpleNode.createNode(node04, 8),
			node09 = SimpleNode.createNode(node04, 9),
			node10 = SimpleNode.createNode(node04, 10),
			node11 = SimpleNode.createNode(node05, 11);
		
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
		SimpleNode<Integer> node01 = new SimpleNode<>(1);
		assertArrayEquals(new Integer[] {1}, toArray(node01.breadthFirstSearch()));

		SimpleNode<Integer>
			node02 = SimpleNode.createNode(node01, 2),
			node03 = SimpleNode.createNode(node01, 3),
			node04 = SimpleNode.createNode(node01, 4),
			node05 = SimpleNode.createNode(node02, 5),
			node06 = SimpleNode.createNode(node02, 6),
			node07 = SimpleNode.createNode(node03, 7),
			node08 = SimpleNode.createNode(node04, 8),
			node09 = SimpleNode.createNode(node04, 9),
			node10 = SimpleNode.createNode(node04, 10),
			node11 = SimpleNode.createNode(node05, 11);
		
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
