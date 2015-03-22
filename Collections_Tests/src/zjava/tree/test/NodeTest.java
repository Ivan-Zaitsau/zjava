package zjava.tree.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.Test;

import zjava.datastructure.TreeNode;
import zjava.datastructure.Treez;

public class NodeTest {
	
	// - edge cases

	@Test(timeout = 200)
	public void dfsIteratorAtEnd() {
		TreeNode<Integer> node01 = new TreeNode<Integer>(1);
		Iterator<TreeNode<Integer>> dfsIter = node01.depthFirstSearch().iterator();
		assertEquals((Integer) 1, dfsIter.next().getValue());
		boolean noSuchElement = false;
		try {
			dfsIter.next();
		}
		catch(NoSuchElementException nsee) {
			noSuchElement = true;
		}
		assertTrue(noSuchElement);
	}

	@Test(timeout = 200)
	public void bfsIteratorAtEnd() {
		TreeNode<Integer> node01 = new TreeNode<Integer>(1);
		Iterator<TreeNode<Integer>> bfsIter = node01.breadthFirstSearch().iterator();
		assertEquals((Integer) 1, bfsIter.next().getValue());
		boolean noSuchElement = false;
		try {
			bfsIter.next();
		}
		catch(NoSuchElementException nsee) {
			noSuchElement = true;
		}
		assertTrue(noSuchElement);		
	}
	
	@Test(timeout = 200)
	@SuppressWarnings("unused")
	public void testEarlyRewindDfs01() {
		TreeNode<Integer> node01 = new TreeNode<Integer>(1);
		Iterator<TreeNode<Integer>> dfsIter = Treez.depthFirstSearch(node01, new Treez.NodeFilter<TreeNode<Integer>>() {
			public boolean isIgnored(TreeNode<Integer> node) {
				return (node.getValue() & 1) == 0;
			}
		}).iterator();
		TreeNode<Integer>
			node02 = TreeNode.createNode(node01, 2),
			node03 = TreeNode.createNode(node01, 3);
		assertEquals((Integer) 1, dfsIter.next().getValue());
		node02.setValue(7);
		TreeNode<Integer>
			node4 = TreeNode.createNode(node02, 4),
			node5 = TreeNode.createNode(node02, 5);
		assertEquals((Integer) 7, dfsIter.next().getValue());
		assertEquals((Integer) 5, dfsIter.next().getValue());
		assertEquals((Integer) 3, dfsIter.next().getValue());
		assertFalse(dfsIter.hasNext());
	}
	
	@Test(timeout = 200)
	@SuppressWarnings("unused")
	public void testEarlyRewindBfs01() {
		TreeNode<Integer> node01 = new TreeNode<Integer>(1);
		Iterator<TreeNode<Integer>> bfsIter = Treez.breadthFirstSearch(node01, new Treez.NodeFilter<TreeNode<Integer>>() {
			public boolean isIgnored(TreeNode<Integer> node) {
				return (node.getValue() & 1) == 0;
			}
		}).iterator();
		TreeNode<Integer>
			node02 = TreeNode.createNode(node01, 2),
			node03 = TreeNode.createNode(node01, 3);
		assertEquals((Integer) 1, bfsIter.next().getValue());
		node02.setValue(7);
		TreeNode<Integer>
			node4 = TreeNode.createNode(node02, 4),
			node5 = TreeNode.createNode(node02, 5);
		assertEquals((Integer) 7, bfsIter.next().getValue());
		assertEquals((Integer) 3, bfsIter.next().getValue());
		assertEquals((Integer) 5, bfsIter.next().getValue());
		assertFalse(bfsIter.hasNext());
	}
	
	// - basic tests
	
	private <E> Object[] toArray(Iterable<TreeNode<E>> iterable) {
		List<E> values = new ArrayList<E>();
		for (TreeNode<E> node : iterable)
			values.add(node.getValue());
		return values.toArray();
	}

	@Test(timeout = 200)
	public void testDfs01() {
		TreeNode<Integer> node01 = new TreeNode<Integer>(1);
		assertArrayEquals(new Integer[] {1}, toArray(node01.depthFirstSearch()));
		
		TreeNode<Integer>
			node02 = TreeNode.createNode(node01, 2),
			node03 = TreeNode.createNode(node01, 3),
			node04 = TreeNode.createNode(node01, 4),
			node05 = TreeNode.createNode(node02, 5),
			node06 = TreeNode.createNode(node02, 6),
			node07 = TreeNode.createNode(node03, 7),
			node08 = TreeNode.createNode(node04, 8),
			node09 = TreeNode.createNode(node04, 9),
			node10 = TreeNode.createNode(node04, 10),
			node11 = TreeNode.createNode(node05, 11);
		
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
		TreeNode<Integer> node01 = new TreeNode<Integer>(1);
		assertArrayEquals(new Integer[] {1}, toArray(node01.breadthFirstSearch()));

		TreeNode<Integer>
			node02 = TreeNode.createNode(node01, 2),
			node03 = TreeNode.createNode(node01, 3),
			node04 = TreeNode.createNode(node01, 4),
			node05 = TreeNode.createNode(node02, 5),
			node06 = TreeNode.createNode(node02, 6),
			node07 = TreeNode.createNode(node03, 7),
			node08 = TreeNode.createNode(node04, 8),
			node09 = TreeNode.createNode(node04, 9),
			node10 = TreeNode.createNode(node04, 10),
			node11 = TreeNode.createNode(node05, 11);
		
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
