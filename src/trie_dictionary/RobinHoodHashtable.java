package trie_dictionary;

/**
 * A hashtable implementation using Robin Hood hashing for efficient collision
 * resolution.
 * <p>
 * Robin Hood hashing is an open addressing scheme that reduces variance in
 * probe lengths
 * by "stealing" from the rich (elements with short probe distances) and giving
 * to the
 * poor (elements with long probe distances). When a collision occurs, if the
 * new element
 * has a longer probe distance than the existing element, they swap positions.
 * </p>
 * <p>
 * This implementation stores Edge objects and uses the first character of edge
 * labels
 * as the key. The hashtable automatically rehashes when the load factor reaches
 * 90%.
 * </p>
 * <p>
 * Key features:
 * <ul>
 * <li>Open addressing with linear probing</li>
 * <li>Probe length balancing via Robin Hood strategy</li>
 * <li>Automatic resizing at 90% load factor</li>
 * <li>Prime number capacities for better distribution</li>
 * </ul>
 * </p>
 * 
 * @author George Leonidou
 * @author Nikolas Pomiloridis
 * @version 1.0
 */

public class RobinHoodHashtable {

    /** Array storing the edges in the hashtable. */
    private Edge[] table;

    /**
     * The maximum probe length currently in the hashtable.
     * Used to optimize search operations by limiting the search range.
     */
    private int maxProbeLength;

    /** The total capacity of the hashtable array. */
    private int capacity;

    /** The number of edges currently stored in the hashtable. */
    private int size;

    /**
     * Constructs a new RobinHoodHashtable with default initial capacity of 3.
     */
    public RobinHoodHashtable() {
        this(3);
    }

    /**
     * Constructs a new RobinHoodHashtable with the specified capacity.
     * <p>
     * Initializes an empty hashtable with the given capacity and sets
     * the maximum probe length to 0.
     * </p>
     * 
     * @param capacity the initial capacity of the hashtable
     */
    private RobinHoodHashtable(int capacity) {
        size = 0;
        this.capacity = capacity;
        maxProbeLength = 0;
        table = new Edge[capacity];
    }

    /**
     * Inserts an edge into the hashtable.
     * <p>
     * Uses Robin Hood hashing: during insertion, if a collision occurs and the
     * new element has traveled further than the existing element, they swap
     * positions.
     * This reduces variance in probe lengths and improves average search time.
     * </p>
     * <p>
     * Automatically triggers a rehash if adding the element would cause the load
     * factor to reach or exceed 90%.
     * </p>
     * 
     * @param edge the edge to insert; ignored if null
     */
    public void insert(Edge edge) {
        if (edge == null)
            return;

        // If adding element will cause load factor to be >= 90% rehash
        if ((size + 1) / (float) capacity >= 0.9)
            rehash();

        int index = hash(edge.getLabel().charAt(0));
        insertHelper(edge, index, 0);
        size++;
    }

    /**
     * Recursive helper method for inserting an edge using Robin Hood hashing.
     * <p>
     * Implements the core Robin Hood logic: compares the probe length of the
     * element being inserted with the probe length of the element at the current
     * position. If the new element has traveled further, they swap and the
     * displaced element continues searching.
     * </p>
     * 
     * @param edge        the edge to insert
     * @param index       the current index being examined
     * @param probeLength the number of positions this edge has traveled from its
     *                    hash position
     */
    private void insertHelper(Edge edge, int index, int probeLength) {
        if (table[index] == null || !table[index].isOccupied()) {
            table[index] = edge;
            table[index].setOccupied(true);

            if (probeLength > maxProbeLength) {
                maxProbeLength = probeLength;
            }
            return;
        }

        // Check whether to swap elements
        int key = hash(table[index].getLabel().charAt(0));
        int existingProbeLength = index - key;

        if (existingProbeLength < 0)
            existingProbeLength = capacity - key + index;

        if (probeLength <= existingProbeLength) {
            // Insert the same edge at the next index
            insertHelper(edge, (index + 1) % capacity, probeLength + 1);
        } else {
            // Swap elements and insert temp at next index
            Edge temp = table[index];
            table[index] = edge;

            if (probeLength > maxProbeLength) {
                maxProbeLength = probeLength;
            }

            insertHelper(temp, (index + 1) % capacity, existingProbeLength + 1);
        }
    }

    /**
     * Searches for an edge by the first character of its label.
     * <p>
     * Uses the maxProbeLength to limit the search range, providing efficient
     * lookups even with collisions. The search only examines positions within
     * maxProbeLength of the hash position.
     * </p>
     * 
     * @param firstChar the first character of the edge label to search for
     * @return the Edge whose label starts with the specified character, or null if
     *         not found
     */
    public Edge search(char firstChar) {
        int key = hash(firstChar);

        for (int i = 0; i <= maxProbeLength; i++) {
            int index = (key + i) % capacity;

            if (table[index] == null || table[index].isOccupied() == false)
                return null;

            else if (table[index].getLabel().charAt(0) == firstChar)
                return table[index];
        }

        return null;
    }

    /**
     * Rehashes the hashtable to a larger capacity.
     * <p>
     * Creates a new hashtable with increased capacity (following a sequence of
     * prime numbers: 3 → 7 → 11 → 17 → 23 → 29) and reinserts all existing
     * elements. This maintains performance as the hashtable grows.
     * </p>
     * <p>
     * The rehashing process recalculates all hash positions and probe lengths
     * based on the new capacity.
     * </p>
     */
    public void rehash() {
        int newSize;
        switch (size) {
            case 3:
                newSize = 7;
                break;
            case 7:
                newSize = 11;
                break;
            case 11:
                newSize = 17;
                break;
            case 17:
                newSize = 23;
                break;
            case 23:
                newSize = 29;
                break;
            default:
                newSize = 29;
                break;
        }

        RobinHoodHashtable ht = new RobinHoodHashtable(newSize);

        for (int i = 0; i < this.capacity; i++) {
            if (table[i] != null && table[i].isOccupied())
                ht.insert(this.table[i]);
        }

        copy(ht);
    }

    /**
     * Copies the contents of another hashtable into this one.
     * <p>
     * Used during rehashing to replace the current hashtable's internal state
     * with the newly created hashtable's state.
     * </p>
     * 
     * @param ht the hashtable to copy from
     */
    private void copy(RobinHoodHashtable ht) {
        this.capacity = ht.capacity;
        this.maxProbeLength = ht.maxProbeLength;
        this.size = ht.size;

        this.table = new Edge[ht.capacity];

        for (int i = 0; i < ht.capacity; i++) {
            this.table[i] = ht.table[i];
        }
    }

    /**
     * Computes the hash value for a character.
     * <p>
     * Maps both uppercase and lowercase letters to the same hash value by
     * normalizing to lowercase. The hash is computed as the position in the
     * alphabet modulo the capacity.
     * </p>
     * <p>
     * Formula:
     * <ul>
     * <li>Uppercase: (char - 'A') % capacity</li>
     * <li>Lowercase: (char - 'a') % capacity</li>
     * </ul>
     * </p>
     * 
     * @param firstChar the character to hash
     * @return the hash value (index) for the character
     */
    private int hash(char firstChar) {
        if (firstChar >= 'A' && firstChar <= 'Z')
            return (firstChar - 'A') % capacity;
        else
            return (firstChar - 'a') % capacity;
    }

    /**
     * Deletes an entrie from the hashtable.
     * 
     * <p>
     * Virtually deletes the given edge from the hashtable by setting its occupied
     * flag to
     * false.
     * </p>
     * 
     * @param edge The edge to be deleted
     */
    public void delete(Edge edge) {
        if (edge == null)
            return;
        edge.setOccupied(false);
    }

    /**
     * Returns a string representation of the hashtable.
     * <p>
     * Shows the contents of each slot in the hashtable. Empty or unoccupied
     * slots are represented by "_", while occupied slots show the edge label.
     * </p>
     * 
     * @return a string showing all hashtable slots separated by spaces
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < capacity; i++) {
            if (table[i] == null || !table[i].isOccupied())
                sb.append("_ ");
            else
                sb.append(table[i].getLabel() + ' ');
        }

        return sb.toString();
    }

    /**
     * Retrieves all edges stored in the hashtable as a linked list.
     * <p>
     * Iterates through the entire hashtable array and collects all occupied
     * edges into a SinglyLinkedList. Useful for traversing all edges without
     * regard to their hash positions.
     * </p>
     * 
     * @return a SinglyLinkedList containing all edges in the hashtable
     */
    public SinglyLinkedList getAllEdges() {
        SinglyLinkedList list = new SinglyLinkedList();

        for (int i = 0; i < capacity; i++) {
            if (table[i] != null && table[i].isOccupied())
                list.insert(table[i]);
        }

        return list;
    }

    /**
     * Computes the total memory usage of this hash table structure, including:
     * <ul>
     *     <li>The object itself (header, references, and primitive fields)</li>
     *     <li>The internal array overhead for storing {@code Edge} references</li>
     *     <li>The memory usage of all non-null {@code Edge} objects in the array</li>
     * </ul>
     *
     * @return total memory footprint of this object and all contained edges, in bytes
     */
    public long getSize() {
        long bytes = 16 + 8 + 4 + 4 + 4;

        // Array overhead for Edge[]
        if (table != null) {
            bytes += 16 + 4 + 4;

            // Each array slot (reference): 8 bytes
            bytes += table.length * 8;

            // Add size of each Edge object
            for (int i = 0; i < table.length; i++) {
                if (table[i] != null)
                    bytes += table[i].getSize();
            }
        }

        return bytes;
    }

    /**
     * Main method for testing the Robin Hood hashtable implementation.
     * <p>
     * Demonstrates collision handling by inserting characters that hash to the
     * same bucket, shows how the hashtable grows through rehashing, and verifies
     * search functionality. Includes detailed output showing the state of the
     * hashtable after each operation.
     * </p>
     * 
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("=== ROBIN HOOD HASHING TEST (Single Characters) ===\n");

        RobinHoodHashtable ht = new RobinHoodHashtable();

        // Letters chosen so that: (letter - 'a') % 3 == 0 → collision chain
        String[] strongCollisions = {
                "a", // 0 % 3 = 0
                "d", // 3 % 3 = 0
                "g", // 6 % 3 = 0
                "j", // 9 % 3 = 0
                "m", // 12 % 3 = 0
                "p", // 15 % 3 = 0
                "s", // 18 % 3 = 0
                "v", // 21 % 3 = 0
                "y" // 24 % 3 = 0
        };

        System.out.println("Inserting characters that all hash to the same bucket...\n");

        for (String s : strongCollisions) {
            System.out.println("Insert: " + s);
            ht.insert(new Edge(s, null));
            System.out.println("Table: " + ht);
            System.out.println("Current maxProbeLength = " + ht.maxProbeLength);
            System.out.println("---------------------------------------------");
        }

        System.out.println("\n=== TEST SEARCH ===");
        for (String s : strongCollisions) {
            char c = s.charAt(0);
            Edge e = ht.search(c);
            System.out.println("Search '" + c + "' → " +
                    (e != null ? e.getLabel() : "NOT FOUND"));
        }

        System.out.println("\n=== INSERT MORE LETTERS (triggers rehash) ===");

        // now use letters that map to other buckets
        String[] moreLetters = { "b", "c", "e", "f", "h", "i" };

        for (String s : moreLetters) {
            System.out.println("Insert: " + s);
            ht.insert(new Edge(s, null));
            System.out.println("Table: " + ht);
            System.out.println("Current maxProbeLength = " + ht.maxProbeLength);
            System.out.println("---------------------------------------------");
        }

        System.out.println("\n=== FINAL TABLE ===");
        System.out.println(ht);
        System.out.println("Final maxProbeLength = " + ht.maxProbeLength);

        for (String s : strongCollisions) {
            char c = s.charAt(0);
            Edge e = ht.search(c);
            System.out.println("Search '" + c + "' → " +
                    (e != null ? e.getLabel() : "NOT FOUND"));
        }
    }

}