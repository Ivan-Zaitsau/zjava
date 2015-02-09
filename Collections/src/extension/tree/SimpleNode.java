package extension.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * <tt>SimpleNode</tt> class represents a single node in a tree.<br>
 * 
 * @param <E> element contained at a node
 * 
 * @author Ivan Zaitsau
 */
public class SimpleNode<E> implements Node {
	
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
	public static <E> SimpleNode<E> createNode(SimpleNode<E> parent, E value) {
		SimpleNode<E> node = new SimpleNode<>(parent, value);
		parent.getChildren().add(node);
		return node;
	}
	
	private SimpleNode<E> parent;
	private List<Node> children = new ArrayList<>(2);
	private E value;
	
	/**
	 * Constructs a root node with value set to constructor's argument
	 * 
	 * @param value - value to assign to newly created Node
	 */
	public SimpleNode(E value) {
		this.value = value;
	}
	
	// - constructs a Node with specified parent and value
	private SimpleNode(SimpleNode<E> parent, E value) {
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
	public SimpleNode<E> getParent() {
		return parent;
	}
	
	/**
	 * Returns list of node child-nodes
	 * 
	 * @return list of node children
	 */
	public List<Node> getChildren() {
		return children;
	}

	/**
	 * Returns <tt>Iterable</tt> which navigates this node and it's successors
	 * in depth-first search order.
	 * 
	 * @return <tt>Iterable</tt> which navigates this node and it's successors
	 * in depth-first search order.
	 */
	public Iterable<Node> depthFirstSearch() {
		return Trees.depthFirstSearch(this);
	}

	/**
	 * Returns <tt>Iterable</tt> which navigates this node and it's successors
	 * in breadth-first search order.
	 * 
	 * @return <tt>Iterable</tt> which navigates this node and it's successors
	 * in breadth-first search order.
	 */
	public Iterable<Node> breadthFirstSearch() {
		return Trees.breadthFirstSearch(this);
	}
}
