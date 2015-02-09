package extension.tree;

import java.util.List;

/**
 * <tt>Node</tt> interface represents a single node in a tree.<br>
 * 
 * @param <E> element contained at a node
 * 
 * @author Ivan Zaitsau
 */
public interface Node {
	
	/**
	 * Returns list of node child-nodes
	 * 
	 * @return list of node children
	 */
	List<Node> getChildren();

}
