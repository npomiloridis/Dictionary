package trie_dictionary;

/**
 * Represents an edge in a compressed trie connecting a parent node to a child
 * node.
 * <p>
 * In a compressed trie, edges are labeled with strings rather than single
 * characters,
 * allowing multiple characters to be represented in a single edge. This
 * compression
 * reduces the space complexity of the trie structure.
 * </p>
 * <p>
 * Each edge contains:
 * <ul>
 * <li>A label (string) representing the characters on this edge</li>
 * <li>A reference to the child node this edge points to</li>
 * <li>An occupied flag for hashtable management</li>
 * </ul>
 * </p>
 * 
 * @author George Leonidou
 * @author Nikolas Pomiloridis
 * @version 1.0
 */
public class Edge {
	/**
	 * The string label for this edge.
	 * In a compressed trie, this can be a string of any length representing
	 * the sequence of characters along this edge.
	 */
	private String label;
	/**
	 * The child node that this edge points to.
	 * Following this edge leads to the subtree rooted at this child node.
	 */
	private CompressedTrieNode child;
	/**
	 * Flag indicating whether this edge slot is occupied in a hashtable.
	 * Used for hashtable collision resolution and slot management.
	 */
	private boolean occupied;

	/**
	 * Constructs a new Edge with the specified label and child node.
	 * <p>
	 * The occupied flag is initialized to false by default.
	 * </p>
	 * 
	 * @param label the string label for this edge; must not be null
	 * @param child the child node this edge points to; must not be null
	 */
	public Edge(String label, CompressedTrieNode child) {
		this.label = label;
		this.child = child;
		this.occupied = false;
	}

	/**
	 * Gets the label of this edge.
	 * <p>
	 * Returns a new String object to prevent external modification of the internal
	 * label.
	 * </p>
	 * 
	 * @return a copy of the edge's label string
	 */
	public String getLabel() {
		return new String(this.label);
	}

	/**
	 * Sets the label of this edge.
	 * <p>
	 * This method is typically used when splitting edges during insertion
	 * operations
	 * to maintain the compressed trie structure.
	 * </p>
	 * 
	 * @param label the new label string for this edge
	 */
	public void setLabel(String label) {
		this.label = new String(label);
	}

	/**
	 * Gets the child node that this edge points to.
	 * 
	 * @return the child CompressedTrieNode
	 */
	public CompressedTrieNode getChild() {
		return this.child;
	}

	/**
	 * Sets the child node that this edge points to.
	 * <p>
	 * This method is used when restructuring the trie, such as when
	 * splitting edges or inserting new nodes.
	 * </p>
	 * 
	 * @param child the new child node for this edge
	 */
	public void setChild(CompressedTrieNode child) {
		this.child = child;
	}

	/**
	 * Sets the occupied status of this edge.
	 * <p>
	 * This flag is used by the hashtable implementation to track whether
	 * a slot contains a valid edge or is available for insertion.
	 * </p>
	 * 
	 * @param occupied true to mark this edge slot as occupied, false otherwise
	 */
	public void setOccupied(boolean occupied) {
		this.occupied = occupied;
	}

	/**
	 * Checks whether this edge slot is marked as occupied.
	 * <p>
	 * Used by the hashtable for collision resolution and slot management.
	 * </p>
	 * 
	 * @return true if this edge slot is occupied, false otherwise
	 */
	public boolean isOccupied() {
		return occupied;
	}

	/**
	 * Computes the approximate memory footprint of this {@code Edge} object,
	 * including:
	 * <ul>
	 *     <li>The object itself (header, primitive fields, and references)</li>
	 *     <li>The {@code String} label object, if present (object header, fields, padding)</li>
	 *     <li>The {@code char[]} inside the label, including its array overhead and character data</li>
	 * </ul>
	 *
	 * @return approximate memory footprint of this Edge and its label in bytes
	 */
	public long getSize() {
		long bytes = 16 + 1 + 7 + 8 + 8;

		// String object overhead and character data
		if (label != null) {
			bytes += 16 + 8 + 4 + 4; // = 32 bytes per String

			// char array overhead
			// Array overhead: 16 bytes
			// Array length: 4 bytes
			// Padding: 4 bytes
			bytes += 16 + 4 + 4; // = 24 bytes

			// Character data: 2 bytes per char
			bytes += label.length() * 2;
		}
		return bytes;
	}
}
