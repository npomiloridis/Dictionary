/**
 * Provides utility classes for the probabilistic analysis and space evaluation
 * of trie-based data structures.
 *
 * <p>
 * This package supports Part B of the assignment, where words are generated
 * according to a probabilistic model and used to measure the memory consumption
 * of different data structures (e.g., tries and compressed tries).
 * </p>
 *
 * <p>
 * The package includes:
 * </p>
 *
 * <ul>
 *   <li>
 *     {@link WordGenerator} – Generates random words based on predefined
 *     probability distributions for characters and word lengths.
 *   </li>
 *   <li>
 *     {@link MemoryCounter} – Provides methods for estimating or measuring
 *     memory usage of data structures during experimentation.
 *   </li>
 *   <li>
 *     {@link HashSet} – Stores generated words and serves as a baseline data
 *     structure for comparison with trie-based implementations.
 *   </li>
 * </ul>
 *
 * <p>
 * Together, these classes enable controlled experiments that relate theoretical
 * probability models to practical memory usage, allowing comparison between
 * standard and compressed trie representations.
 * </p>
 *
 * @author Nikolas Pomiloridis
 */
package memory_test;
