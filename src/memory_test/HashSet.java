package memory_test;
/**
 * A lightweight custom hash set implementation for storing unique {@code String}
 * values using open addressing with linear probing.
 *
 * <p>This class is optimized for fast insertion and lookup during word
 * generation in experiments. It supports:
 * <ul>
 *   <li>Dynamic resizing (rehashing) when a load factor threshold is exceeded</li>
 *   <li>Power-of-two table sizes for efficient modular arithmetic</li>
 *   <li>A custom multiplicative shifting hash function</li>
 * </ul>
 *
 * <p>Only {@code String} keys are supported, and removal is not implemented.
 */
public class HashSet {
    
    /**
     * The underlying hash table storing strings. Empty slots hold {@code null}.
     */
    private String[] table;

    /**
     * The current number of entries stored in the hash set.
     */
    private int size;
    
    /**
     * The current capacity of the table (always a power of two).
     */
    private int capacity; 
    
    /**
     * Maximum allowed load factor before triggering rehashing.
     */
    private final double loadFactor = 0.7;

    /**
     * Constructs a new {@code HashSet} with initial capacity 16.
     */
    public HashSet() {
        capacity = 16;
        table = new String[capacity];
        size = 0;
    }

    /**
     * Constructs a new {@code HashSet} with a specified minimum capacity.
     * <p>
     * The actual capacity is rounded up to the next power of two.
     *
     * @param initialCapacity the requested minimum capacity
     */
    public HashSet(int initialCapacity) {
        capacity = nextPowerOfTwo(initialCapacity);
        table = new String[capacity];
        size = 0;
    }

    /**
     * Returns the next power of two greater than or equal to {@code n}.
     *
     * @param n the input value
     * @return a power of two >= n
     */
    private int nextPowerOfTwo(int n) {
        int p = 1;
        while (p < n) p <<= 1;
        return p;
    }

    /**
     * Computes a hash value for the given string using a simple multiplicative
     * hashing scheme and keeps it within a 32-bit positive range.
     *
     * @param s the string to hash
     * @return a hash code for the string
     */
    private int hash(String s) {
        long h = 1;
        long mod = 0x7fffffffL; // keep in 32-bit positive range

        for (int i = 0; i < s.length(); i++) {
            h = ((h << 1) * s.charAt(i)) & mod; 
        }

        return (int)h;
    }

    /**
     * Returns the number of elements currently stored in the set.
     *
     * @return the number of stored keys
     */
    public int size() {
        return size;
    }

    /**
     * Checks whether the specified string exists in the set using linear probing.
     *
     * @param s the string to search for
     * @return {@code true} if the string exists, {@code false} otherwise
     */
    public boolean contains(String s) {
        int index = hash(s) & (capacity - 1);

        while (table[index] != null) {
            if (table[index].equals(s)) return true;
            index = (index + 1) & (capacity - 1);
        }
        return false;
    }

    /**
     * Inserts a string into the set, if it is not already present.
     * <p>
     * If adding the element would exceed the load factor threshold,
     * the table automatically doubles in size and rehashes all existing entries.
     *
     * @param s the string to insert
     * @return {@code true} if the insertion succeeded,
     *         {@code false} if the value already exists
     */
    public boolean add(String s) {
        if (contains(s)) return false;

        if ((double)(size + 1) / capacity > loadFactor) {
            rehash();
        }

        int index = hash(s) & (capacity - 1);

        while (table[index] != null) {
            index = (index + 1) & (capacity - 1);
        }

        table[index] = s;
        size++;
        return true;
    }

    /**
     * Doubles the capacity of the table and re-inserts all existing keys.
     * <p>
     * This method is triggered automatically when the load factor exceeds 0.7.
     */
    private void rehash() {
        String[] oldTable = table;

        capacity *= 2;
        table = new String[capacity];
        size = 0;

        for (int i = 0; i < oldTable.length; i++) {
            if (oldTable[i] != null) {
                add(oldTable[i]);
            }
        }
    }
}
