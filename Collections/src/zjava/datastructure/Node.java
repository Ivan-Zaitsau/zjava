package zjava.datastructure;

import java.util.List;

/**
 * <tt>Node</tt> interface represents a single node in a tree or tree-like data structure.<br>
 * 
 * @param <T> actual node type
 * 
 * @author Ivan Zaitsau
 */
public interface Node<T extends Node<T>> {
	
	/**
	 * Returns list of node child-nodes<br>
	 * 
	 * <p><b>Important:</b><br>List object must remain the same on
	 * subsequent calls during certain operations (tree traversal, for example).
	 * List contents may change over time.
	 * 
	 * @return list of node children
	 */
	List<T> getChildren();
}
