/**
 * <h1>Autocomplete System</h1>
 *
 * <p>
 * An autocomplete system that suggests words based on a given prefix.
 * The system uses a compressed trie data structure and learns word
 * frequencies from text files.
 * </p>
 *
 * <h2>What It Does</h2>
 * <p>This program helps complete words by:</p>
 * <ul>
 *   <li>Finding words that start with a prefix</li>
 *   <li>Ranking words by how often they appear in text</li>
 *   <li>Predicting what letter comes next</li>
 *   <li>Calculating average word frequency</li>
 * </ul>
 *
 * <h2>Files</h2>
 * <ul>
 *   <li><code>CompressedTrie.java</code> - Stores words in a trie structure</li>
 *   <li><code>CompressedTrieNode.java</code> - Node in the trie</li>
 *   <li><code>Edge.java</code> - Connection between compressed trie nodes with a label</li>
 *   <li><code>RobinHoodHashtable.java</code> - Hash table for storing edges</li>
 *   <li><code>SinglyLinkedList.java</code> - List for edges</li>
 *   <li><code>MinHeap.java</code> - Priority queue for ranking words</li>
 *   <li><code>Dictionary.java</code> - Loads words and tracks frequency</li>
 *   <li><code>PrefixAnalyzer.java</code> - Finds top words and makes predictions</li>
 *   <li><code>Menu.java</code> - User interface</li>
 * </ul>
 *
 * <h2>How to Run</h2>
 * <p><strong>1. Compile:</strong></p>
 * <pre>{@code
 * javac UC1366149_UC1367923/*.java
 * }</pre>
 *
 * <p><strong>2. Run:</strong></p>
 * <pre>{@code
 * java UC1366149_UC1367923.Menu dictionary.txt text.txt
 * }</pre>
 *
 * <p>
 * The first file should contain valid words. The second file is used
 * to count how often words appear.
 * </p>
 *
 * <h2>Menu Options</h2>
 * <ol>
 *   <li>Find top <em>k</em> words with a prefix</li>
 *   <li>Get average frequency for a prefix</li>
 *   <li>Predict next letter after a prefix</li>
 *   <li>Exit</li>
 * </ol>
 *
 * <h2>Example</h2>
 * <pre>
 * Enter option (1 - 4): 1
 * Give the number of words you need: 3
 * Give the prefix: cat
 * The top 3 words with prefix cat are:
 * category
 * catch
 * catalog
 * </pre>
 *
 * <h2>How It Works</h2>
 * <p><strong>Compressed Trie:</strong> Stores words efficiently by combining
 * single paths into one edge. Faster than checking each letter separately.</p>
 *
 * <p><strong>Importance:</strong> Each word has an importance value that
 * increases when the word appears in the text file.</p>
 *
 * <p><strong>Top K Words:</strong> Uses a min-heap to keep track of the
 * <em>k</em> most important words without sorting everything.</p>
 *
 * <p><strong>Next Letter Prediction:</strong> Looks at all possible next
 * letters and picks the one that leads to more frequently used words.</p>
 *
 * <h2>Input Files</h2>
 *
 * <p><strong>Dictionary File:</strong> One word per line</p>
 * <pre>
 * apple
 * application
 * apply
 * </pre>
 *
 * <p><strong>Text File:</strong> Any text for frequency counting</p>
 * <pre>
 * I need to apply for the job.
 * The apple was red.
 * </pre>
 *
 * <h2>Notes</h2>
 * <ul>
 *   <li>Words are cleaned (lowercase, no special characters)</li>
 *   <li>Empty words are ignored</li>
 *   <li>If fewer than <em>k</em> words exist, returns all available words</li>
 * </ul>
 */

package trie_dictionary;
