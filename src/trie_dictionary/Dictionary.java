package trie_dictionary;

import java.io.File;
import java.util.Scanner;

/**
 * Manages a dictionary of words stored in a compressed trie data structure.
 * <p>
 * The Dictionary class provides functionality to:
 * <ul>
 * <li>Load words from a file into the trie</li>
 * <li>Update word importance based on frequency in a text file</li>
 * <li>Clean and validate words (remove non-alphabetical characters)</li>
 * </ul>
 * </p>
 * <p>
 * All words are stored in lowercase and only contain alphabetical characters.
 * Special characters at the beginning and end of words are automatically removed
 * during the cleaning process.
 * </p>
 * 
 * @author George Leonidou
 * @author Nikolas Pomiloridis
 * @version 1.0
 */

public class Dictionary {

    /** The compressed trie storing all dictionary words. */
    private CompressedTrie dictionary;

    /**
     * Constructs a new empty Dictionary with an initialized compressed trie.
     */
    public Dictionary() {
        this.dictionary = new CompressedTrie();
    }

    /**
     * Loads awords from a file into the dictionry.
     * <p>
     * Reads the file word by word, cleans each word to remove special characters,
     * and inserts valid words into the compressed trie. All words are converted to lowercase.
     * </p>
     * 
     * @param filename the path to the file containing words to load
     * @throws RuntimeException if the file cannot be read or does not exist
     */
    public void loadDictionary(String filename) {
        try {
            File file = new File(filename);
            Scanner in = new Scanner(file);
            while (in.hasNext()) {
                String word = cleanWord(in.next());
                if (word.length() > 0)
                    this.dictionary.insert(word);
            }
            in.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

    }

    /**
     * Updates word importance values based on word frequency in a text file.
     * <p>
     * Reads the file word by word, and for each word that exists in the dictionary,
     * increments its importance value. Words are cleaned and validated before lookup.
     * Words containing special characters (after cleaning) or not found in the
     * dictionary are skipped.
     * </p>
     * 
     * @param filename the path to the text file used for calculating word frequencies
     * @throws RuntimeException if the file cannot be read or does not exist
     */
    public void updateImportance(String filename) {
        try {
            File file = new File(filename);
            Scanner in = new Scanner(file);
            while (in.hasNext()) {
                String word = cleanWord(in.next());
                if (word.length() == 0)
                    continue;

                // if word contains non alphabetical character after clean up skip it
                // (dictionary only contains words without special characters)
                if(hasSpecial(word))
                    continue;

                if (this.dictionary.search(word))
                    this.dictionary.findPrefixNode(word).node.incrementImportance();
            }
            in.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage()); // En eimai sigouros an en swsto throw exception
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Retrieves the underlying compressed trie containing the dictionary.
     * 
     * @return the CompressedTrie storing all dictionary words
     */
    public CompressedTrie getTrie() {
        return dictionary;
    }

    /**
     * Cleans a word by removing special characters and converting to lowercase.
     * <p>
     * The cleaning process:
     * <ol>
     * <li>Converts the word to lowercase</li>
     * <li>Finds the first alphabetical character (a-z)</li>
     * <li>Finds the last alphabetical character (a-z)</li>
     * <li>Returns the substring containing only the alphabetical portion</li>
     * </ol>
     * </p>
     * <p>
     * Examples:
     * <ul>
     * <li>"Hello!" → "hello"</li>
     * <li>"'world'" → "world"</li>
     * <li>"123abc456" → "abc"</li>
     * <li>"!!!" → "" (empty string)</li>
     * </ul>
     * </p>
     * 
     * @param word the word to clean; can be null
     * @return the cleaned word containing only lowercase letters, or empty string
     *         if no alphabetical characters are found
     */
    public static String cleanWord(String word) {
        if (word == null || word.length() == 0)
            return "";
        word = word.toLowerCase();
        int first = 0, last = 0;
        for (first = 0; first < word.length(); first++) {
            if (word.charAt(first) >= 'a' && word.charAt(first) <= 'z')
                break;
        }
        for (last = word.length() - 1; last >= 0; last--) {
            if (word.charAt(last) >= 'a' && word.charAt(last) <= 'z')
                break;
        }
        if (first > last || first >= word.length())
            return "";
        return word.substring(first, last + 1);
    }

    /**
     * Checks if a word contains any non-alphabetical characters.
     * 
     * @param word the word to check
     * @return true if the word contains any non-alphabetical characters, false otherwise
     */
    public static boolean hasSpecial(String word) {
        for (int i = 0; i < word.length(); i++)
            if ((word.charAt(i) < 'a' || word.charAt(i) > 'z') && (word.charAt(i) < 'A' || word.charAt(i) > 'Z'))
                return true;
        return false;
    }
}
