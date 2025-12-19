package trie_dictionary;

/**
 * Represents a single node in a compressed trie data structure.
 * <p>
 * Each node stores:
 * <ul>
 * <li>A hashtable of outgoing edges to child nodes</li>
 * <li>A flag indicating whether this node represents the end of a complete
 * word</li>
 * <li>An importance value for ranking or prioritization purposes</li>
 * </ul>
 * </p>
 * <p>
 * The node uses a Robin Hood hashtable for efficient edge storage and retrieval
 * based on the first character of edge labels.
 * </p>
 * 
 * @author George Leonidou
 * @author Nikolas Pomiloridis
 * @version 1.0
 */

public class CompressedTrieNode {
	/**
	 * Hashtable storing edges from this node to child nodes.
	 * Edges are indexed by the first character of their labels for fast lookup.
	 */
	private RobinHoodHashtable hashtable;
	/**
	 * Flag indicating whether this node marks the end of a complete word in the
	 * trie.
	 * When true, the path from root to this node forms a valid word.
	 */
	private boolean isEndOfWord;
	/**
	 * Importance or priority value associated with this node.
	 * Can be used for ranking suggestions in autocomplete or other applications.
	 */
	private int importance;

	/**
	 * Constructs a new CompressedTrieNode with default values.
	 * <p>
	 * Initializes an empty hashtable for edges, sets isEndOfWord to false,
	 * and importance to 0.
	 * </p>
	 */
	public CompressedTrieNode() {
		this.hashtable = new RobinHoodHashtable();
		this.isEndOfWord = false;
		this.importance = 0;
	}

/**
 * Computes the total memory usage of this compressed trie node, including:
 * <ul>
 *     <li>The object's own JVM header and fields</li>
 *     <li>The memory used by the edge hashtable</li>
 *     <li>The memory of all descendant nodes reachable via edges</li>
 * </ul>
 *
 * @return the total memory footprint of this node and all children (in bytes)
 */
	public long getSize() {
		// Base memory cost
		long bytes = 16 + 8 + 1 + 4 + 3;

		if (hashtable != null)
			bytes += hashtable.getSize();

		// Recursively add child nodes (accessed through edges)
		if (hashtable != null) {
			SinglyLinkedList.Node currentEdge = hashtable.getAllEdges().head;
			while (currentEdge != null) {
				if (currentEdge.edge != null && currentEdge.edge.getChild() != null)
					bytes += currentEdge.edge.getChild().getSize();
				currentEdge = currentEdge.next;
			}
		}

		return bytes;
	}

	/**
	 * Inserts an edge from this node to a child node.
	 * <p>
	 * The edge is stored in the internal hashtable, indexed by the first
	 * character of its label for efficient retrieval.
	 * </p>
	 * 
	 * @param edge the edge to insert; must not be null
	 */
	public void insertEdge(Edge edge) {
		this.hashtable.insert(edge);
	}

	/**
	 * Retrieves an edge from this node by the first character of its label.
	 * <p>
	 * This method finds the appropriate edge to follow during trie traversal.
	 * </p>
	 * 
	 * @param c the first character of the edge label to search for
	 * @return the Edge starting with the specified character, or null if no such
	 *         edge exists
	 */
	public Edge getEdgeByFirstChar(char c) {
		return this.hashtable.search(c);
	}

	/**
	 * Retrieves all edges from this node.
	 * <p>
	 * Returns a list containing all edges stored in this node's hashtable,
	 * useful for traversing all children or performing operations on the entire
	 * subtree.
	 * </p>
	 * 
	 * @return a SinglyLinkedList containing all edges from this node
	 */
	public SinglyLinkedList getAllEdges() {
		return this.hashtable.getAllEdges();
	}

	/**
	 * Checks whether this node represents the end of a complete word.
	 * 
	 * @return true if this node marks the end of a word, false otherwise
	 */
	public boolean isEndOfWord() {
		return isEndOfWord;
	}

	/**
	 * Sets whether this node represents the end of a complete word.
	 * 
	 * @param isEndOfWord true to mark this node as the end of a word, false
	 *                    otherwise
	 */
	public void setIsEndOfWord(boolean isEndOfWord) {
		this.isEndOfWord = isEndOfWord;
	}

	/**
	 * Gets the importance value of this node.
	 * <p>
	 * The importance value can be used for ranking words in autocomplete
	 * suggestions, search results, or other prioritization schemes.
	 * </p>
	 * 
	 * @return the current importance value
	 */
	public int getImportance() {
		return importance;
	}

	/**
	 * Sets the importance value of this node.
	 * 
	 * @param importance the new importance value to assign
	 */
	public void setImportance(int importance) {
		this.importance = importance;
	}

	/**
	 * Increments the importance value of this node by 1.
	 * <p>
	 * This method is useful for tracking word frequency or usage patterns,
	 * allowing the trie to adapt to user behavior over time.
	 * </p>
	 */
	public void incrementImportance() {
		this.importance++;
	}
}