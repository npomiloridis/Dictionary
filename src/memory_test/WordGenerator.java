package memory_test;

import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

/**
 * The {@code WordGenerator} class builds statistical models of word lengths and
 * character frequencies from a given dictionary file, and uses them to generate
 * artificial words or full dictionary files.
 * 
 * <p>It computes:
 * <ul>
 *   <li>Character probabilities and their cumulative distribution (CDF)</li>
 *   <li>Word-length probabilities and CDF</li>
 * </ul>
 * 
 * The generator can create dictionaries of:
 * <ul>
 *   <li>Random-length words (length drawn from the empirical distribution)</li>
 *   <li>Fixed-length words</li>
 * </ul>
 * 
 * All generated dictionaries contain only unique words.
 * 
 * @author Nikolas Pomiloridis
 * @author Giorgos Leonidou
 * @version 1.0
 */
public class WordGenerator {
    
    private final static int alphabetSize = 26;
    private String filename;
    private double[] charCDF;
    private double[] lengthCDF;
    private double[] charProb;
    private double[] lengthProb;
    private static final String dir = "dictionaries/";

    /**
     * Constructs a {@code WordGenerator} that computes probability distributions
     * based on the contents of the given dictionary file.
     *
     * @param filename the path to the dictionary file used for statistical analysis
     */
    public WordGenerator(String filename){
        this.filename = filename;

        calculateProbabilities();
        calculateCDFs();
    }

    /**
     * Returns the empirical probability distribution of characters
     * ('a' to 'z') computed from the input dictionary.
     *
     * @return an array of size 26 containing character probabilities
     */
    public double[] getCharProb(){
        return charProb;
    }

    /**
     * Returns the empirical probability distribution of word lengths computed
     * from the input dictionary.
     *
     * @return an array where index {@code i} contains P(word has length i)
     */
    public double[] getLengthProb(){
        return lengthProb;
    }

    /**
     * Computes character and word-length probability distributions from the
     * input dictionary file. This method is called automatically in the constructor.
     * <p>
     * It counts:
     * <ul>
     *   <li>How many words have each length</li>
     *   <li>How frequently each character occurs</li>
     * </ul>
     */
    private void calculateProbabilities(){
        int size = maxLength() + 1;
        lengthProb = new double[size];
        charProb = new double[alphabetSize];

        try{
            // Create input stream
            File input = new File(filename);
            Scanner scan = new Scanner(input);

            int[] lengthCount = new int[size];
            int[] charCount = new int[alphabetSize];
            int totalWords = 0;
            int totalChars = 0;

            while(scan.hasNext()){
                String word = scan.next();

                // Increase measurements for length
                lengthCount[word.length()]++;
                totalWords ++;

                // Increase measurements for characters
                for(int i=0; i<word.length(); i++){
                    if(word.charAt(i) < 'a' || word.charAt(i) > 'z'){
                        System.out.println("Invalid word given: " + word);
                        continue;
                    }
                    charCount[word.charAt(i) - 'a'] ++;
                    totalChars++;
                }
            }

            for(int i=0; i<size; i++){
                lengthProb[i] = (double)lengthCount[i] / totalWords;
            }

            for(int i=0; i<alphabetSize; i++){
                charProb[i] = (double)charCount[i] / totalChars;
            }

            scan.close();
        }catch(Exception e){
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

    }

    /**
     * Converts the probability distributions into cumulative distribution
     * functions (CDFs) for efficient sampling during random generation.
     */
    private void calculateCDFs(){
        charCDF = new double[alphabetSize];
        lengthCDF = new double[lengthProb.length];

        lengthCDF[0] = lengthProb[0];
        for(int i=1; i<lengthProb.length; i++){
            lengthCDF[i] = lengthProb[i] + lengthCDF[i-1];
        }

        charCDF[0] = charProb[0];
        for(int i=1; i<charProb.length; i++){
            charCDF[i] = charProb[i] + charCDF[i-1];
        }
    }

    /**
     * Generates a random word whose length is drawn from the empirical
     * length distribution, and whose characters follow the empirical
     * character distribution.
     *
     * @return a pseudo-random word
     */
    public String generateWord(){
        StringBuilder word = new StringBuilder();

        for(int i=0; i<randomLength(); i++){
            word.append(randomChar());
        }
        return word.toString();
    }

    /**
     * Generates a random word with a specified fixed length, where each character
     * is sampled independently from the empirical character distribution.
     *
     * @param length the desired word length
     * @return a pseudo-random word of the specified length
     */
    public String generateWord(int length){
        StringBuilder word = new StringBuilder();

        for(int i=0; i<length; i++){
            word.append( randomChar());
        }
        return word.toString();
    }

    /**
     * Generates a dictionary containing the specified number of unique words.
     * Word lengths follow the empirical distribution.
     *
     * <p>The output file is written to {@code dictionaries/random-length_SIZE.txt}
     * where {@code SIZE} is the number of generated words.
     *
     * @param size number of unique words to generate
     */
    public void generateDictionary(int size){
        try{
            FileWriter output = new FileWriter(dir + "random-length_" + size +".txt");
            HashSet hs = new HashSet(size);
            
            String word = generateWord();
            output.write(word);
            hs.add(word);
            for(int i=1; i<size; i++){
                output.write('\n');
                word = generateWord();
                while(hs.add(word) == false)
                    word = generateWord();
                output.write(word);
            }

            output.close();
            
        }catch(Exception e){
            System.out.println(e.getMessage());
            System.err.println(e.getStackTrace());
            System.exit(1);
        }
    }

    /**
     * Generates a dictionary of unique words with a fixed length.
     *
     * <p>The output file is written to {@code dictionaries/lengthL_SIZE.txt}
     * where {@code L} is the fixed length.
     *
     * @param size the number of unique words to generate
     * @param wordLength the fixed length of each word
     */
    public void generateDictionary(int size, int wordLength){
        try{
            
            FileWriter output = new FileWriter(dir + "length" + wordLength + "_" + size +".txt");
            HashSet hs = new HashSet(size);

            String word = generateWord(wordLength);
            output.write(word);
            hs.add(word);
            for(int i=1; i<size; i++){
                output.write('\n');
                word = generateWord(wordLength);
                while(hs.add(word) == false)
                    word = generateWord(wordLength);
                output.write(word);
            }

            output.close();
            
        }catch(Exception e){
            System.out.println(e.getMessage());
            System.err.println(e.getStackTrace());
            System.exit(1);
        }
    }

    /**
     * Generates a pseudo-random character according to the empirical character CDF.
     *
     * @return a randomly generated lowercase letter ('a'–'z')
     */
    private char randomChar(){
        double random = Math.random();

        for(int i=0; i<charCDF.length; i++){
            if(random < charCDF[i])
                return (char)('a' + i);
        }

        return 'z';
    }

    /**
     * Generates a pseudo-random word length using the empirical length CDF.
     *
     * @return an integer representing a random word length
     */
    private int randomLength(){
        double random = Math.random();

        for(int i=1; i<lengthCDF.length; i++){
            if(random < lengthCDF[i])
                return i;
        }

        return 0;
    }

    /**
     * Scans the input dictionary and returns the maximum word length found.
     * This determines the size of the length distribution arrays.
     *
     * @return the length of the longest word in the dictionary
     */
    private int maxLength(){
        int maxLength = 0;
        try{
            File input = new File(filename);
            Scanner scan = new Scanner(input);

            while(scan.hasNext()){
                String word = scan.next();
                if(word.length() > maxLength)
                    maxLength = word.length();
            }

            scan.close();
        }catch(Exception e){
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
        return maxLength;
    }

    /**
     * Debug method that prints the CDFs for characters and word lengths.
     * Intended only for internal inspection.
     */
    @SuppressWarnings("unused")
    private void printCDFs(){
        for(int i=0; i< alphabetSize; i++){
            System.out.println((char)('a' + i) + ": " + charCDF[i]);
        }

        System.out.println();
        for(int i=0; i< lengthCDF.length; i++){
            System.out.println("Length " + i + ": " + lengthCDF[i]);
        }
    }

    /**
     * Main execution method. Generates several dictionary files of both fixed
     * and random lengths using predefined configurations.
     *
     * @param args unused
     */
    public static void main(String[] args){
        if(args.length < 1){
            System.err.println("Error no file parameter given!");
            System.exit(1);
        }
        WordGenerator wg = new WordGenerator(args[0]);

        // Generate Random length dictionaries
        System.out.println("Generating random length words...\n");
        wg.generateDictionary(10000);
        wg.generateDictionary(100000);
        wg.generateDictionary(500000);
        wg.generateDictionary(1000000);

        int[] sizes = { 10000, 100000, 500000, 1000000 };
        int[] lengths = { 5, 7, 9, 11, 14, 18, 24, 28, 32 };
        int[] sizesFor3 = {5000, 10000, 15000, 17500};

        System.out.println("Generating words of length " + 3 + "...\n");
        long maxPossible3 = (long) Math.pow(26, 3);
        for(int size : sizesFor3){
            if (maxPossible3 < size) {
                System.out.println(
                    "ERROR: Cannot generate " + size +
                    " unique words of length " + 3 +
                    " (maximum is " + maxPossible3 + "). Skipping.\n"
                );
                continue;
            }

            wg.generateDictionary(size, 3);
        }

        for (int L : lengths) {
            System.out.println("Generating words of length " + L + "...\n");

            long maxPossible = (long) Math.pow(26, L);

            for (int size : sizes) {

                // Safety check: impossible request → skip
                if (maxPossible < size) {
                    System.out.println(
                        "ERROR: Cannot generate " + size +
                        " unique words of length " + L +
                        " (maximum is " + maxPossible + "). Skipping.\n"
                    );
                    continue;
                }

                wg.generateDictionary(size, L);
            }
        }
    }
}
