package memory_test;

import java.util.Scanner;
import java.io.File;
import java.io.PrintWriter;

/**
 * The {@code MemoryCounter} class computes the memory usage of two dictionary
 * data structures: {@link Trie} and {@link CompressedTrie}. It reads dictionary
 * files, inserts all words into each structure, and outputs the total memory
 * footprint as returned by their {@code getSize()} methods.
 *
 * <p>This class is used for experimental evaluation of space efficiency under
 * various dictionary sizes and fixed word lengths. It generates two output files:
 * <ul>
 *   <li><strong>trie-memoryX.txt</strong> – memory usage of the standard trie</li>
 *   <li><strong>compressed-trie-memoryX.txt</strong> – memory usage of the compressed trie</li>
 * </ul>
 * where X identifies the experiment batch.
 *
 * <p>The input dictionary files must exist in the {@code dictionaries/} folder.
 * 
 * @author Nikolas Pomiloridis
 * @author Giorgos Leonidou
 * @version 1.0
 */
public class MemoryCounter{

    /**
     * Computes the memory usage of a standard {@link Trie} by inserting all words
     * found in the given dictionary file.
     *
     * @param filename the path to the dictionary file containing one word per line
     * @return the total memory usage of the constructed trie, in bytes
     */
    public static long getSizeTrie(String filename){
        trie_dictionary.Trie trie = new trie_dictionary.Trie();
        try {
            File file = new File(filename);
            Scanner in = new Scanner(file);
            while (in.hasNext()) {
                trie.insert(in.next());
            }
            in.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        return trie.getSize();
    }

    /**
     * Computes the memory usage of a {@link CompressedTrie} by inserting all words
     * found in the given dictionary file.
     *
     * @param filename the path to the dictionary file containing one word per line
     * @return the total memory usage of the constructed compressed trie, in bytes
     */
    public static long getSizeCompressedTrie(String filename) {
        trie_dictionary.CompressedTrie cTrie = new trie_dictionary.CompressedTrie();
        try {
            File file = new File(filename);
            Scanner in = new Scanner(file);
            while (in.hasNext()) {
                cTrie.insert(in.next());
            }
            in.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        return cTrie.getSize();
    }

        
    /**
     * Main method that performs memory measurements for several experimental
     * dictionary configurations. It computes:
     * <ul>
     *   <li>Random-length dictionaries of sizes 10k, 100k, 500k, 1M</li>
     *   <li>Fixed-length dictionaries for a variety of lengths (3, 5, 7, 9, ...)</li>
     *   <li>Ensures that impossible dictionary requests (e.g., more words than 26^L)
     *       are skipped safely</li>
     * </ul>
     *
     * <p>The results are written into:
     * <ul>
     *   <li>{@code compressed-trie-memory.txt}</li>
     *   <li>{@code trie-memory.txt}</li>
     * </ul>
     *
     * @param args unused
     */
    public static void main(String[] args) {
        String dir = "dictionaries/";
        try{
            PrintWriter outputCT = new PrintWriter("compressed-trie-memory.txt");
            PrintWriter outputT = new PrintWriter("trie-memory.txt");
            
            System.out.println("Computing for random lengths...\n");

            outputCT.print("RANDOM LENGTH:\n");
            outputT.print("RANDOM LENGTH:\n");

            outputCT.print(getSizeCompressedTrie(dir + "random-length_10000.txt"));
            outputCT.print('\t');
            outputCT.print(getSizeCompressedTrie(dir + "random-length_100000.txt"));
            outputCT.print('\t');
            outputCT.print(getSizeCompressedTrie(dir + "random-length_500000.txt"));
            outputCT.print('\t');
            outputCT.print(getSizeCompressedTrie(dir + "random-length_1000000.txt"));
            outputCT.print('\t');
            outputCT.println();
            outputCT.println();
            
            outputT.print(getSizeTrie(dir + "random-length_10000.txt"));
            outputT.print('\t');
            outputT.print(getSizeTrie(dir + "random-length_100000.txt"));
            outputT.print('\t');
            outputT.print(getSizeTrie(dir + "random-length_500000.txt"));
            outputT.print('\t');
            outputT.print(getSizeTrie(dir + "random-length_1000000.txt"));
            outputT.print('\t');
            outputT.println();
            outputT.println();
            

            int[] sizes = { 10000, 100000, 500000, 1000000 };
            int[] lengths = { 5, 7, 9, 11, 14, 18, 24, 28, 32 };
            int[] sizesFor3 = { 5000, 10000, 15000, 17500};

            System.out.println("Computing for length 3...\n");

            outputCT.println("LENGTH " + 3 + ":");
            outputT.println("LENGTH " + 3 + ":");

            for(int size : sizesFor3){

                String filename = "length" + 3 + "_";

                long maxPossible = (long) Math.pow(26, 3);

                if (maxPossible < size) {
                        continue;
                }

                outputCT.print(getSizeCompressedTrie(dir + filename + size + ".txt"));
                outputCT.print('\t');
                outputT.print(getSizeTrie(dir + filename + size + ".txt"));
                outputT.print('\t');

            }

            outputCT.println();
            outputT.println();
            outputCT.println();
            outputT.println();

            for (int L : lengths) {
                System.out.println("Computing for length " + L + "...\n");

                outputCT.println("LENGTH " + L + ":");
                outputT.println("LENGTH " + L + ":");

                String filename = "length" + L + "_";

                long maxPossible = (long) Math.pow(26, L);

                for (int size : sizes) {

                    // Safety check: impossible request → skip
                    if (maxPossible < size) {
                        continue;
                    }

                    outputCT.print(getSizeCompressedTrie(dir + filename + size + ".txt"));
                    outputCT.print('\t');
                    outputT.print(getSizeTrie(dir + filename + size + ".txt"));
                    outputT.print('\t');
                }
                outputCT.println();
                outputT.println();
                outputCT.println();
                outputT.println();
            }

            outputCT.close();
            outputT.close();
        }catch(Exception e){
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}