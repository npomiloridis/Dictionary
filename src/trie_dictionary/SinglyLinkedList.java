package trie_dictionary;

/**
 * A singly linked list implementation that stores Edge objects.
 * <p>
 * This data structure is used to store collections of edges in the compressed trie,
 * particularly for maintaining all edges from a node. The list supports insertion at 
 * the head and search operations based on the first character of edge labels.
 * </p>
 * <p>
 * Operations:
 * <ul>
 * <li>Insert: O(1) - adds new edges at the head</li>
 * <li>Search by character: O(n) - linear scan through the list</li>
 * </ul>
 * </p>
 * 
 * @author George Leonidou
 * @author Nikolas Pomiloridis
 * @version 1.0
 */

public class SinglyLinkedList {
	/**
	 * Internal node class representing a single element in the linked list.
	 * <p>
	 * Each node contains an Edge and a reference to the next node in the list.
	 * This is a static nested class as it doesn't require access to the outer
	 * class's instance variables.
	 * </p>
	 */
	static class Node {
		/** The Edge object stored in this node. */
		Edge edge; 
		/**
		 * Reference to the next node in the linked list.
		 * Null if this is the last node.
		 */
		Node next;
		
		/**
		 * Constructs a new Node containing the specified edge.
		 * <p>
		 * The next reference is initialized to null.
		 * </p>
		 * 
		 * @param value the Edge to store in this node
		 */
		Node(Edge value){
			this.edge = value;
			this.next = null;
		}
	}

	/**
	 * The head node of the linked list.
	 * Null if the list is empty.
	 */
	protected Node head;
	
	/**
	 * Constructs an empty singly linked list.
	 * <p>
	 * Initializes the head reference to null, indicating an empty list.
	 * </p>
	 */
	public SinglyLinkedList () { 
		this.head = null;
	}
	
	/**
	 * Inserts an edge at the head of the linked list.
	 * <p>
	 * This operation runs in O(1) constant time as it always inserts
	 * at the beginning of the list. If the list is empty, the edge
	 * becomes the head node.
	 * </p>
	 * 
	 * @param edge the Edge to insert into the list; must not be null
	 */
	public void insert(Edge edge) { 
		if (this.head == null) {
			this.head = new Node(edge);
			return;
		}
		Node newHead = new Node(edge);
		newHead.next = this.head;
		this.head = newHead;
	}
	
	/**
	 * Retrieves an edge from the list whose label starts with the specified character.
	 * <p>
	 * This method performs a linear search through the list, comparing the first
	 * character of each edge's label with the target character. Returns the first
	 * matching edge found, or null if no match exists.
	 * </p>
	 * <p>
	 * Time Complexity: O(n) where n is the number of edges in the list.
	 * </p>
	 * 
	 * @param c the character to search for at the start of edge labels
	 * @return the first Edge whose label starts with the specified character,
	 *         or null if no such edge exists
	 */
	public Edge getEdge (char c) {
	    Node tester = this.head;
	    while (tester != null) {
	    	if(tester.edge.getLabel().charAt(0) == c)
	    		return tester.edge;
	    	tester = tester.next;
	    }
	    return null;
	}
	
	/**
	 * Returns a string representation of the linked list.
	 * <p>
	 * The format shows each edge followed by " -> " and ends with "null"
	 * to indicate the end of the list. This is useful for debugging and
	 * visualizing the list structure.
	 * </p>
	 * <p>
	 * Example output: "edge1 -> edge2 -> edge3 -> null"
	 * </p>
	 * 
	 * @return a string representation of the list showing all edges in order
	 */
	public String toString() {
		Node current = this.head;
		StringBuilder str = new StringBuilder();
		while (current != null) {
			str.append(current.edge + " -> ");
			current = current.next;
		}
		str.append("null");
		return str.toString();
	}
}
