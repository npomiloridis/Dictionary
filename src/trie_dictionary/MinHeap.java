package trie_dictionary;

/**
 * A min-heap implementation for prioritizing strings based on importance values.
 * <p>
 * This heap maintains the min-heap property where each parent node has an importance
 * value less than or equal to its children. When importance values are equal, strings
 * are ordered lexicographically (in reverse order to maintain min-heap semantics).
 * </p>
 * <p>
 * The heap is implemented using a 1-indexed array, where for any node at index i:
 * <ul>
 * <li>Parent is at index i/2</li>
 * <li>Left child is at index 2*i</li>
 * <li>Right child is at index 2*i+1</li>
 * </ul>
 * </p>
 * <p>
 * Common operations:
 * <ul>
 * <li>Insert: O(log n)</li>
 * <li>Delete Min: O(log n)</li>
 * <li>Get Top: O(1)</li>
 * </ul>
 * </p>
 * 
 * @author George Leonidou
 * @author Nikolas Pomiloridis
 * @version 1.0
 */
public class MinHeap{

    /**
     * Represents a single element in the heap containing a string and its importance.
     * <p>
     * HeapNodes are compared first by importance (lower importance is higher priority),
     * and then lexicographically by contents when importance values are equal.
     * </p>
     */
    public class HeapNode{

        /** The string content stored in this heap node. */
        public String contents;

        /**
         * The importance value used for prioritization.
         * Lower values indicate higher priority in the min-heap.
         */
        public int importance;

        /**
         * Constructs a new HeapNode with the specified contents and importance.
         * 
         * @param contents the string to store in this node
         * @param importance the priority value for this node
         */
        public HeapNode(String contents, int importance){
            this.contents = contents;
            this.importance = importance;
        }
    }

    /**
     * Array storing the heap elements.
     * Uses 1-based indexing (index 0 is unused) for simplified parent/child calculations.
     */
    private HeapNode[] contents;

    /** The maximum number of elements the heap can store. */
    private int maxSize;

    /** The current number of elements stored in the heap. */
    private int currentlyStored;

    /**
     * Constructs a new MinHeap with the specified maximum capacity.
     * <p>
     * The heap uses 1-based indexing, so the internal array is allocated
     * with size maxSize + 1.
     * </p>
     * 
     * @param maxSize the maximum number of elements this heap can hold
     */
    public MinHeap(int maxSize){
        this.maxSize = maxSize;
        this.currentlyStored = 0;
        this.contents = new HeapNode[maxSize + 1];
    }

    /**
     * Checks whether the heap is full.
     * 
     * @return true if the heap has reached maximum capacity, false otherwise
     */
    public boolean isFull(){
        return this.currentlyStored >= this.maxSize;
    }

    /**
     * Inserts a new element into the heap with the specified contents and importance.
     * <p>
     * The element is initially placed at the end of the heap and then percolated
     * up to maintain the min-heap property. Time complexity: O(log n).
     * </p>
     * 
     * @param contents the string content to insert
     * @param importance the importance value for prioritization
     * @throws IllegalStateException if the heap is full
     */
    public void insert(String contents, int importance){
        if (isFull()) 
            throw new IllegalStateException("Heap is full");

        HeapNode newNode = new HeapNode(contents, importance);
        this.currentlyStored++;
        this.contents[currentlyStored] = newNode;
        percolateUp(this.currentlyStored);
    }

    /**
     * Inserts a HeapNode directly into the heap.
     * <p>
     * This overloaded method allows insertion of pre-constructed HeapNode objects.
     * The node is placed at the end and percolated up to maintain heap order.
     * Time complexity: O(log n).
     * </p>
     * 
     * @param node the HeapNode to insert into the heap
     * @throws IllegalStateException if the heap is full
     */
    public void insert(HeapNode node){
        if (isFull()) 
            throw new IllegalStateException("Heap is full");

        this.currentlyStored++;
        this.contents[currentlyStored] = node;
        percolateUp(this.currentlyStored);
    }
    
    /**
     * Checks whether the heap is empty.
     * 
     * @return true if the heap contains no elements, false otherwise
     */
    private boolean isEmpty(){
        return this.currentlyStored == 0;
    }   

    /**
     * Removes the minimum element (root) from the heap.
     * <p>
     * The last element in the heap replaces the root, and then percolates down
     * to restore the min-heap property. If the heap is empty, this method does nothing.
     * Time complexity: O(log n).
     * </p>
     */
    public void deleteMin(){
        if (isEmpty()) {
            return;
        }
        contents[1] = contents[currentlyStored]; // switch last and first node
        currentlyStored--;
        percolateDown(1);
    }

    /**
     * Retrieves the minimum element (root) without removing it.
     * <p>
     * In a min-heap, this is always the element at index 1 with the lowest
     * importance value. Time complexity: O(1).
     * </p>
     * 
     * @return the HeapNode at the root of the heap, or null if heap is empty
     */
    public HeapNode getTop(){
        return this.contents[1];
    }

    /**
     * Moves a node up the heap to maintain the min-heap property.
     * <p>
     * Starting from the given index, the node is repeatedly swapped with its parent
     * as long as it has higher priority than the parent. This restores the heap
     * property after insertion.
     * </p>
     * 
     * @param index the starting position of the node to percolate up
     */
    private void percolateUp(int index){
        HeapNode temp = this.contents[index];
        while (index > 1 && isHigherPriority(temp, contents[index/2])) {
            this.contents[index] = this.contents[index / 2];
            index = index / 2; 
        }
        this.contents[index] = temp;
    }

    /**
     * Moves a node down the heap to maintain the min-heap property.
     * <p>
     * Starting from the given index, the node is repeatedly swapped with its
     * smaller child (or the higher priority child if both are equal) until the
     * heap property is restored. This is used after deletion operations.
     * </p>
     * 
     * @param index the starting position of the node to percolate down
     */
    private void percolateDown(int index){
        HeapNode k = this.contents[index];
        int j;
        while (2 * index <= this.currentlyStored) {
            j = 2 * index;
            if (j + 1 <= this.currentlyStored && isHigherPriority(contents[j+1], contents[j])) {
                j++;
            }
            if (isHigherPriority(this.contents[j], k)) {
                this.contents[index] = this.contents[j];
                index = j;
            } else {
                break;
            }
        }
        this.contents[index] = k;
    }

    /**
     * Compares two HeapNodes to determine priority ordering.
     * <p>
     * Node1 has higher priority than node2 if:
     * <ul>
     * <li>node1 has lower importance value, OR</li>
     * <li>importance values are equal AND node1's contents are lexicographically greater</li>
     * </ul>
     * </p>
     * 
     * @apiNote A node with heigher priority should be placed heigher up in the heap.
     * 
     * @param node1 the first node to compare
     * @param node2 the second node to compare
     * @return true if node1 has higher priority than node2, false otherwise
     */
    private boolean isHigherPriority(HeapNode node1, HeapNode node2) {
    if (node1.importance < node2.importance) {
        return true;
    }
    if (node1.importance == node2.importance && 
        node1.contents.compareTo(node2.contents) > 0) {
        return true;
    }
    return false;
    }

    /**
     * Performs heap sort by extracting all elements in priority order.
     * <p>
     * This method destructively empties the heap while extracting elements.
     * Elements are returned in sorted order based on importance (heighest first),
     * with lexicographic ordering as a tiebreaker.
     * </p>
     * <p>
     * Time complexity: O(n log n) where n is the number of elements.
     * </p>
     * <p>
     * @apiNote This method empties the heap. After calling this method, the heap
     * will be empty.
     * </p>
     * 
     * @return an array of strings sorted by priority (heighest importance first)
     */
    public String[] heapSort(){
        String[] sortedArray = new String[this.currentlyStored];
            for (int i = this.currentlyStored; i >= 1; i--){
            sortedArray[i-1] = getTop().contents;
            deleteMin();
        }
        return sortedArray;
    }
}