package zjava.datastructure;

import java.util.ArrayList;
import java.util.List;

/**
 * <tt>TreeNode</tt> class represents a single node in a tree with some value assigned.<br>
 * 
 * @param <E> element contained at a node
 * 
 * @since Zjava 1.0
 * 
 * @author Ivan Zaitsau
 */
public class TreeNode<E> implements Node<TreeNode<E>> {
	
	/**
	 * Adds new child node to given parent node, passed as parameter.
	 * 
	 * @param parent - node to which newly created node is assigned as a child-node
	 * @param value - value assigned to newly created node
	 * 
	 * @return new <tt>Node</tt> assigned to given <tt>parent</tt> node as a
	 *         child with value
	 * 
	 */
	public static <E> TreeNode<E> createNode(TreeNode<E> parent, E value) {
		TreeNode<E> node = new TreeNode<E>(parent, value);
		parent.getChildren().add(node);
		return node;
	}
	
	private TreeNode<E> parent;
	private final List<TreeNode<E>> children = new ArrayList<TreeNode<E>>(2);
	private E value;
	
	/**
	 * Constructs a root node with value set to constructor's argument
	 * 
	 * @param value - value to assign to newly created Node
	 */
	public TreeNode(E value) {
		this.value = value;
	}
	
	// - constructs a Node with specified parent and value
	private TreeNode(TreeNode<E> parent, E value) {
		this(value);
		this.parent = parent;
	}

	/**
	 * Returns value assigned to this node
	 * 
	 * @return value assigned to this node
	 */
	public E getValue() {
		return value;
	}
	
	/**
	 * Updates value assigned to this node with value passed as method argument
	 * 
	 * @param value - value to assign to this Node
	 */
	public void setValue(E value) {
		this.value = value;
	}
	
	/**
	 * Returns parent of this node or <tt>null</tt> if this node is a root
	 * 
	 * @return parent of this node or <tt>null</tt> if this node is a root
	 */
	public TreeNode<E> getParent() {
		return parent;
	}
	
	/**
	 * Returns list of node child-nodes
	 * 
	 * @return list of node children
	 */
	public List<TreeNode<E>> getChildren() {
		return children;
	}

	/**
	 * Returns <tt>Iterable</tt> which navigates this node and it's successors
	 * in depth-first search order.
	 * 
	 * @return <tt>Iterable</tt> which navigates this node and it's successors
	 * in depth-first search order.
	 */
	public Iterable<TreeNode<E>> depthFirstSearch() {
		return Treez.depthFirstSearch(this);
	}

	/**
	 * Returns <tt>Iterable</tt> which navigates this node and it's successors
	 * in breadth-first search order.
	 * 
	 * @return <tt>Iterable</tt> which navigates this node and it's successors
	 * in breadth-first search order.
	 */
	public Iterable<TreeNode<E>> breadthFirstSearch() {
		return Treez.breadthFirstSearch(this);
	}
}
