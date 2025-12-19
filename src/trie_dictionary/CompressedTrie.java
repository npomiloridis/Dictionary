package trie_dictionary;
/**
 * A compressed trie data structure for efficient string storage and retrieval.
 * <p>
 * A compressed trie optimizes space by merging chains of nodes with single
 * children
 * into single edges labeled with strings. This implementation supports
 * insertion,
 * search, and prefix-based lookups.
 * </p>
 * <p>
 * Example usage:
 * 
 * <pre>
 * CompressedTrie trie = new CompressedTrie();
 * trie.insert("car");
 * trie.insert("cat");
 * boolean found = trie.search("car"); // returns true
 * </pre>
 * </p>
 * 
 * @author George Leonidou
 * @author Nikolas Pomiloridis
 * @version 1.0
 */

public class CompressedTrie {
	/** The root node of the compressed trie. */
	CompressedTrieNode root;

	/**
	 * Result object returned by prefix search operations.
	 * <p>
	 * Contains the node where the prefix ends and any remaining suffix
	 * from the edge label that extends beyond the prefix.
	 * </p>
	 */
	public class PrefixResult {
		/** The node at which the prefix search terminates. */
		public CompressedTrieNode node;

		/**
		 * The remaining portion of the edge label after matching the prefix.
		 * Empty string if the prefix exactly matches the edge label.
		 */
		public String suffix;

		/**
		 * Constructs a new PrefixResult with the specified node and suffix.
		 * 
		 * @param node   the node where the prefix ends
		 * @param suffix the remaining suffix from the edge label
		 */
		public PrefixResult(CompressedTrieNode node, String suffix) {
			this.node = node;
			this.suffix = suffix;
		}
	}

	/**
	 * Constructs an empty compressed trie with a root node.
	 */
	public CompressedTrie() {
		this.root = new CompressedTrieNode();
	}

	/**
	 * Inserts a word into the compressed trie.
	 * <p>
	 * If the word shares a common prefix with existing edges, the trie
	 * structure is adjusted by splitting edges as necessary to maintain
	 * the compressed property.
	 * </p>
	 * 
	 * @param word the word to insert into the trie; must not be null
	 */
	public void insert(String word) {
		if (this.root == null) {
			this.root = new CompressedTrieNode();
		}
		insertHelper(this.root, word);
	}

	/**
	 * Computes the total memory usage of the entire CompressedTrie, including:
	 * <ul>
	 * <li>The CompressedTrie object itself (JVM header + fields)</li>
	 * <li>The memory usage of the root node</li>
	 * <li>All descendant nodes and edges recursively (via
	 * {@code root.getSize()})</li>
	 * </ul>
	 * Total base cost: <strong>24 bytes</strong>.
	 *
	 * @return total memory footprint of the CompressedTrie in bytes
	 */
	public long getSize() {
		long bytes = 16 + 8;

		if (root != null)
			bytes += root.getSize();

		return bytes;
	}

	/**
	 * Recursive helper method for inserting a word into the trie.
	 * <p>
	 * This method handles edge splitting when a partial match is found,
	 * creating intermediate nodes to maintain the compressed trie structure.
	 * </p>
	 * 
	 * @param node the current node being processed
	 * @param word the remaining portion of the word to insert
	 */
	private void insertHelper(CompressedTrieNode node, String word) {
		// If word is
		if (word.length() == 0) {
			node.setIsEndOfWord(true);
			return;
		}
		Edge common = node.getEdgeByFirstChar(word.charAt(0));
		// If there's not an edge with word.charAt[0]
		if (common == null) {
			CompressedTrieNode newNode = new CompressedTrieNode();
			newNode.setIsEndOfWord(true);
			node.insertEdge(new Edge(word, newNode));
			return;
		}
		// Find prefix
		int i = 0;
		String prefix;
		while (i < word.length() && i < common.getLabel().length() && word.charAt(i) == common.getLabel().charAt(i)) {
			i++;
		}
		prefix = word.substring(0, i);
		String edgeRest = common.getLabel().substring(i);
		String wordRest = word.substring(i);
		if (edgeRest.length() == 0) {
			insertHelper(common.getChild(), wordRest);
			return;
		}
		CompressedTrieNode splitNode = new CompressedTrieNode();
		splitNode.insertEdge(new Edge(edgeRest, common.getChild()));
		common.setChild(splitNode);
		common.setLabel(prefix);
		// insertHelper(splitNode, wordRest);
		if (wordRest.length() == 0)
			splitNode.setIsEndOfWord(true); // The split node represents a complete word
		else
			insertHelper(splitNode, wordRest);
	}

	/**
	 * Searches for a word in the compressed trie.
	 * 
	 * @param word the word to search for
	 * @return true if the word exists as a complete word in the trie, false
	 *         otherwise
	 */
	public boolean search(String word) {
		if (this.root == null)
			return false;
		return searchHelper(this.root, word);
	}

	/**
	 * Recursive helper method for searching a word in the trie.
	 * <p>
	 * Traverses edges by matching prefixes, ensuring that edge labels
	 * are completely matched before proceeding to child nodes.
	 * </p>
	 * 
	 * @param node the current node being examined
	 * @param word the remaining portion of the word to search for
	 * @return true if the word is found and marked as end of word, false otherwise
	 */
	private boolean searchHelper(CompressedTrieNode node, String word) {
		if (word.length() == 0)
			return node.isEndOfWord();
		Edge common = node.getEdgeByFirstChar(word.charAt(0));
		// If there's not an edge with word.charAt[0]
		if (common == null)
			return false;
		// Find prefix
		if (!word.startsWith(common.getLabel()))
			return false;
		String wordRest = word.substring(common.getLabel().length());
		return searchHelper(common.getChild(), wordRest);
	}

	/**
	 * Finds the node in the trie where the given prefix ends.
	 * <p>
	 * This method is useful for implementing prefix-based operations such as
	 * autocomplete or finding all words with a common prefix.
	 * </p>
	 * 
	 * @param prefix the prefix string to search for
	 * @return a PrefixResult containing the node where the prefix ends and any
	 *         remaining suffix, or null if the prefix is not found
	 * @see PrefixResult
	 */
	public PrefixResult findPrefixNode(String prefix) {
		if (this.root == null)
			return null;
		return findPrefixNodeHelper(this.root, prefix);
	}

	/**
	 * Recursive helper method for finding the node where a prefix ends.
	 * <p>
	 * Handles three cases:
	 * <ul>
	 * <li>Prefix matches exactly to a node boundary</li>
	 * <li>Prefix ends partway through an edge label</li>
	 * <li>Prefix extends beyond an edge and continues into subtree</li>
	 * </ul>
	 * </p>
	 * 
	 * @param node   the current node being examined
	 * @param prefix the remaining portion of the prefix to match
	 * @return a PrefixResult with the matching node and suffix, or null if no match
	 */
	private PrefixResult findPrefixNodeHelper(CompressedTrieNode node, String prefix) {
		// Base case
		if (prefix.length() == 0)
			return new PrefixResult(node, "");

		Edge edge = node.getEdgeByFirstChar(prefix.charAt(0));
		if (edge == null) // No words start with first char of prefix
			return null;

		// Case 1: Prefix is shorter or equal to edge label
		if (prefix.length() <= edge.getLabel().length()) {
			// Check if prefix is the same as the first letters of label
			boolean samePrefix = true;
			for (int i = 0; i < prefix.length(); i++) {
				if (prefix.charAt(i) != edge.getLabel().charAt(i)) {
					samePrefix = false;
					break;
				}
			}

			if (samePrefix) {
				return new PrefixResult(edge.getChild(), edge.getLabel().substring(prefix.length()));
			} else
				return null;
		} else { // Case 2: prefix is longer
			boolean samePrefix = true;
			for (int i = 0; i < edge.getLabel().length(); i++) {
				if (prefix.charAt(i) != edge.getLabel().charAt(i)) {
					samePrefix = false;
					break;
				}
			}

			if (samePrefix) {
				String remainingPrefix = prefix.substring(edge.getLabel().length());
				return findPrefixNodeHelper(edge.getChild(), remainingPrefix);
			} else
				return null;
		}
	}

	/**
	 * Main method for testing the compressed trie implementation.
	 * <p>
	 * Inserts several words and performs search operations to verify
	 * the correctness of the trie structure.
	 * </p>
	 * 
	 * @param args command line arguments (not used)
	 */
	public static void main(String[] args) {
		CompressedTrie trie = new CompressedTrie();
		// Insert words
		trie.insert("car");
		trie.insert("ca");
		trie.insert("cat");
		trie.insert("dog");
		trie.insert("door");
		trie.insert("do");
		// Words to test
		String[] tests = {
				"car",
				"ca",
				"cat",
				"dog",
				"do",
				"door",
				"cart",
				"c",
				"doll",
				"doe"
		};
		// Run searches
		for (String word : tests) {
			boolean found = trie.search(word);
			System.out.println("search(\"" + word + "\")\t->\t" + found);
		}
	}
}
