package trie_dictionary;

/**
 * Analyzes word prefixes in a compressed trie to provide autocomplete and prediction features.
 * <p>
 * The PrefixAnalyzer offers three main functionalities:
 * <ul>
 * <li>Finding the top k most frequently used words with a given prefix</li>
 * <li>Calculating the average frequency of words starting with a prefix</li>
 * <li>Predicting the most likely next character after a prefix</li>
 * </ul>
 * </p>
 * <p>
 * All analyses are based on word importance values stored in the trie, which
 * typically represent usage frequency from a text.
 * </p>
 * 
 * @author George Leonidou
 * @author Nikolas Leonidou
 * @version 1.0
 */

public class PrefixAnalyzer {

    /** The compressed trie containing the dictionary of words to analyze. */
    private CompressedTrie dictionary;

    /**
     * Constructs a new PrefixAnalyzer for the specified dictionary.
     * 
     * @param dictionary the CompressedTrie containing words to analyze
     */
    public PrefixAnalyzer(CompressedTrie dictionary) {
        this.dictionary = dictionary;
    }

    /**
     * Finds the top k most frequently used words that start with the given prefix.
     * <p>
     * Uses a min-heap of size k to efficiently track the k words with highest
     * importance values. The algorithm maintains a heap containing the k best
     * candidates seen so far. When a new word is encountered:
     * <ul>
     * <li>If the heap is not full, the word is added</li>
     * <li>If the heap is full and the new word has higher importance (or equal importance
     *     but comes alphabetically earlier), the minimum element is removed and the new
     *     word is added</li>
     * </ul>
     * After traversing all words, the heap is sorted to produce the final result.
     * </p>
     * <p>
     * Words are ranked first by importance (higher is better), and when importance
     * values are equal, alphabetically (earlier in dictionary order is better).
     * </p>
     * <p>
     * Time Complexity: O(n log k) where n is the number of words with the prefix.
     * </p>
     * 
     * @param prefix the prefix to search for
     * @param k the number of top words to return
     * @return an array of the top k words sorted by importance (highest first),
     *         or an empty array if no words start with the prefix. May return
     *         fewer than k words if fewer matching words exist.
     */
    public String[] topKFrequentWordsWithPrefix(String prefix, int k) {
        MinHeap heap = new MinHeap(k);

        CompressedTrie.PrefixResult result = dictionary.findPrefixNode(prefix);
        if (result == null)    
            return new String[0];
        fillHeap(heap, result.node, prefix + result.suffix);
        return heap.heapSort();
    }


    /**
     * Recursively fills the heap with words from the trie subtree.
     * <p>
     * Performs a depth-first traversal of the trie starting from the given node.
     * For each complete word encountered, decides whether to add it to the heap
     * based on:
     * <ul>
     * <li>If heap is not full, add the word</li>
     * <li>If heap is full and word has higher importance than the minimum, replace the minimum</li>
     * <li>If importance is equal, use alphabetical order as tiebreaker</li>
     * </ul>
     * </p>
     * 
     * @param heap the MinHeap to fill with top k words
     * @param node the current trie node being processed
     * @param word the word formed by the path from root to this node
     */
    private void fillHeap(MinHeap heap, CompressedTrieNode node, String word) {
        // Base Case: node is null
        if (node == null) {
            return;
        }

        // If node contains a word decide whether or not to add it in the heap
        if (node.isEndOfWord()) {
            if (!heap.isFull())
                heap.insert(word, node.getImportance());
            else if (node.getImportance() > heap.getTop().importance || (node.getImportance() == heap.getTop().importance && word.compareTo(heap.getTop().contents) < 0)) {
                heap.deleteMin();
                heap.insert(word, node.getImportance());
            }
        }

        // Recursively call for all child nodes
        SinglyLinkedList.Node current = node.getAllEdges().head;
        while (current != null) {
            fillHeap(heap, current.edge.getChild(), word + current.edge.getLabel());
            current = current.next;
        }
    }

    /**
     * Calculates the average importance (frequency) of all words starting with the given prefix.
     * <p>
     * Traverses the subtree rooted at the prefix node and computes the mean
     * importance value across all complete words in that subtree.
     * </p>
     * 
     * @param prefix the prefix to analyze
     * @return the average importance value of words with this prefix, or 0 if
     *         no words start with the prefix
     */
    public float getAverageFrequencyOfPrefix(String prefix) {
        CompressedTrie.PrefixResult searchResult = dictionary.findPrefixNode(prefix);
        if (searchResult == null)
            return 0;

        int[] sumAndCount = getSumAndCount(searchResult.node);
        int sum = sumAndCount[0], count = sumAndCount[1];

        if (count == 0)
            return 0;

        return (float) sum / count;
    }

    /**
     * Recursively calculates the sum of importance values and count of words in a subtree.
     * <p>
     * Performs a depth-first traversal to collect statistics about all complete
     * words in the subtree rooted at the given node. Sum and count are performed in a single 
     * method to minimize trie traversal.
     * </p>
     * 
     * @param node the root node of the subtree to analyze
     * @return an array of two integers: [0] = sum of importance values, [1] = count of words
     */
    private int[] getSumAndCount(CompressedTrieNode node) {
        // Base case
        if (node == null)
            return new int[] { 0, 0 };

        int sum = 0, count = 0;
        if (node.isEndOfWord()) {
            sum = node.getImportance();
            count = 1;
        }

        SinglyLinkedList.Node current = node.getAllEdges().head;
        while (current != null) {
            int[] result = getSumAndCount(current.edge.getChild());
            sum += result[0];
            count += result[1];
            current = current.next;
        }

        return new int[]{sum, count};
    }

    /**
     * Predicts the most likely next character to follow the given prefix.
     * <p>
     * The prediction strategy:
     * <ol>
     * <li>If all words with this prefix share the same next character (indicated
     *     by a non-empty suffix in the PrefixResult), return that character</li>
     * <li>Otherwise, examine each possible next character by calculating the average
     *     frequency of words that would result from appending that character</li>
     * <li>Return the character that leads to the subtree with highest average frequency</li>
     * </ol>
     * </p>
     * 
     * @param prefix the prefix to analyze
     * @return the most probable next character based on frequency analysis
     * @throws Exception if no words start with the given prefix, or if prediction
     *                   cannot be performed (e.g., prefix forms a complete word with no extensions)
     */
    public char predictNextLetter(String prefix) throws Exception {
        CompressedTrie.PrefixResult searchResult = dictionary.findPrefixNode(prefix);
        if (searchResult == null)
            throw new Exception("Cannot predict next letter!");

        // If all words with this prefix share the next letter return it
        if (searchResult.suffix.length() > 0)
            return searchResult.suffix.charAt(0);

        // get average frequency of all subtrees
        SinglyLinkedList.Node current = searchResult.node.getAllEdges().head;
        if (current == null) // if node is leaf node
            throw new Exception("No words start with given prefix!");

        float frequency, maxFrequency = Float.NEGATIVE_INFINITY;
        char mostFrequentChar = '\0';

        while (current != null) {
            frequency = getAverageFrequencyOfPrefix(prefix + current.edge.getLabel().charAt(0));
            if (frequency > maxFrequency) {
                maxFrequency = frequency;
                mostFrequentChar = current.edge.getLabel().charAt(0);
            }
            current = current.next;
        }

        if(mostFrequentChar == '\0')
            throw new Exception("Cannot predict next letter!");
        
        return mostFrequentChar;
    }
}
