package trie_dictionary;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Interactive command-line menu for the autocomplete and word analysis system.
 * <p>
 * This class provides a text-based user interface that allows users to:
 * <ul>
 * <li>Find the k most frequently used words with a given prefix</li>
 * <li>Calculate average word frequency for a prefix</li>
 * <li>Predict the most probable next character after a prefix</li>
 * </ul>
 * </p>
 * <p>
 * The program requires two command-line arguments:
 * <ol>
 * <li>Path to a dictionary file containing valid words</li>
 * <li>Path to a text file used to calculate word importance/frequency</li>
 * </ol>
 * </p>
 * <p>
 * Usage: 
 * <ol>
 *  <li> Compilation: {@code javac UC1366149_UC1367923/*.java} </li>
 *  <li> Execution: {@code java UC1366149_UC1367923.Menu dictionary.txt textFile.txt}</li>
 * </p>
 * 
 * @author George Leonidou
 * @author Nikolas Pomiloridis
 * @version 1.0
 */

public class Menu {

    /**
     * Main entry point for the autocomplete system.
     * <p>
     * Initializes the dictionary and word importance data, then presents
     * an interactive menu loop that continues until the user chooses to exit.
     * </p>
     * <p>
     * The method performs the following steps:
     * <ol>
     * <li>Validates command-line arguments</li>
     * <li>Loads the dictionary from the word file</li>
     * <li>Updates word importance based on the text file</li>
     * <li>Enters the interactive menu loop</li>
     * </ol>
     * </p>
     * 
     * @param args command-line arguments: args[0] = dictionary file path,
     *             args[1] = text file path for importance calculation
     */
    public static void main(String[] args) {

        if (args.length != 2) {
            System.err.println("Wrong number of arguments given!\nCorrenct execution command: java Menu wordFile.txt textFile.txt");
            System.exit(1);
        }

        String wordFile = args[0];
        String importanceFile = args[1];
        if (wordFile == null || importanceFile == null) {
            System.err.println("Parameter entered is null!");
            System.exit(1);
        }

        System.out.println("Loading Dictionary...");
        Dictionary dictionary = new Dictionary();
        dictionary.loadDictionary(wordFile);

        System.out.println("Updating Word Importance...");
        dictionary.updateImportance(importanceFile);

        CompressedTrie trie = dictionary.getTrie();
        PrefixAnalyzer analyzer = new PrefixAnalyzer(trie);

        int option = 0;

        Scanner in = new Scanner(System.in);
        do {
            System.out.println("1.\tFind k most used words after prefix");
            System.out.println("2.\tAverage Frequency");
            System.out.println("3.\tFind most probable character");
            System.out.println("4.\tExit");
            System.out.println("Enter option (1 - 4): ");

            // Ensure correct input is given
            boolean correctInputRead = false;
            while (!correctInputRead) {
                try {
                    option = in.nextInt();
                    while (option < 1 || option > 4) {
                        System.out.println("Wrong input, give option again (1 - 4): ");
                        option = in.nextInt();
                    }
                    correctInputRead = true;
                } catch (InputMismatchException e) {
                    // Clean buffer and print error message
                    in.next();
                    System.err.println("Invalid input given. Please enter an integer: ");
                    correctInputRead = false;
                }
            }

            String prefix;
            switch (option) {
                case 1:
                    // Read number of words (k)
                    System.out.println("Give the number of words you need: ");
                    correctInputRead = false;
                    int k = 0;
                    while (!correctInputRead) {
                        try {
                            k = in.nextInt();
                            while (k <= 0) {
                                System.out.println("Wrong input, give number greater than 0: ");
                                k = in.nextInt();
                            }
                            correctInputRead = true;
                        } catch (InputMismatchException e) {
                            // Clean buffer and print error message
                            in.next();
                            System.err.println("Invalid input given. Please enter an integer: ");
                            correctInputRead = false;
                        }
                    }

                    // Read prefix
                    prefix = readPrefixSafely(in);
                    String[] results = analyzer.topKFrequentWordsWithPrefix(prefix, k);
                    if(results.length == k)
                        System.out.println("The top " + k + " words with prefix " + prefix + " are:");
                    else if( results.length > 0 && results.length < k)
                        System.out.println("There are only " + results.length + " words with prefix: ");
                    else
                        System.out.println("There are no words starting with this prefix.");
                    for (int j = 0; j < results.length; j++) {
                        System.out.println(results[j]);
                    }   
                    break;

                case 2:
                    prefix = readPrefixSafely(in);
                    float average = analyzer.getAverageFrequencyOfPrefix(prefix);
                    System.out.println("The average frequency of words with prefix " + prefix + " is: " + average);

                    System.out.println("Give the prefix: ");
                    correctInputRead = false;
                    break;

                case 3:

                    prefix = readPrefixSafely(in);
                    char character;
                    try {
                        character = analyzer.predictNextLetter(prefix);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                        break;
                    }
                    System.out.println("The most probable character after prefix " + prefix + " is: " + character);
                    break;

                case 4:
                    System.out.println("Exiting program.");
                    break;
            }

            System.out.println();
        }while(option!=4);

    in.close();
    }

    /**
     * Safely reads and validates a prefix from user input.
     * <p>
     * Continuously prompts the user until a valid prefix is entered. A valid
     * prefix must:
     * <ul>
     * <li>Contain at least one alphabetical character after cleaning</li>
     * <li>Contain no special characters</li>
     * </ul>
     * </p>
     * <p>
     * The method uses {@link Dictionary#cleanWord(String)} to remove special
     * characters and {@link Dictionary#hasSpecial(String)} to validate the result.
     * </p>
     * 
     * @param inputStream the Scanner to read input from
     * @return a valid, cleaned prefix containing only lowercase letters
     */
    private static String readPrefixSafely(Scanner inputStream) {
        System.out.println("Give the prefix: ");
        String prefix;

        do{
            prefix = Dictionary.cleanWord(inputStream.next());
            if(prefix.length() > 0 && !Dictionary.hasSpecial(prefix))
                return prefix;
            else{
                System.out.println("Invalid prefix! Enter prefix again: ");              
            }
        }while(true);
    }
}