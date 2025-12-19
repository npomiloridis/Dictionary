# Autocomplete System

An autocomplete system that suggests words based on a given prefix. The system uses a compressed trie data structure and learns word frequencies from text files.

## What It Does

This program helps complete words by:
- Finding words that start with a prefix
- Ranking words by how often they appear in text
- Predicting what letter comes next
- Calculating average word frequency

## Files

- `CompressedTrie.java` - Stores words in a trie structure
- `CompressedTrieNode.java` - Node in the trie
- `Edge.java` - Connection between Compressed trie nodes with a label
- `RobinHoodHashtable.java` - Hash table for storing edges
- `SinglyLinkedList.java` - List for edges
- `MinHeap.java` - Priority queue for ranking words
- `Dictionary.java` - Loads words and tracks frequency
- `PrefixAnalyzer.java` - Finds top words and makes predictions
- `Menu.java` - User interface

## How to Run

1. Compile:
```bash
cd src
javac src/trie_dictionary/*.java
```

2. Run:
```bash
java trie_dictionary.Menu dictionary.txt text.txt
```

The first file should contain valid words. The second file is used to count how often words appear.

## Menu Options

1. Find top k words with a prefix
2. Get average frequency for a prefix
3. Predict next letter after a prefix
4. Exit

## Example

```
Enter option (1 - 4): 1
Give the number of words you need: 3
Give the prefix: cat
The top 3 words with prefix cat are:
category
catch
catalog
```

## How It Works

**Compressed Trie**: Stores words efficiently by combining single paths into one edge. Faster than checking each letter separately.

**Importance**: Each word has an importance value that increases when the word appears in the text file.

**Top K Words**: Uses a min-heap to keep track of the k most important words without sorting everything.

**Next Letter Prediction**: Looks at all possible next letters and picks the one that leads to more frequently used words.

## Input Files

**Dictionary File**: One word per line
```
apple
application
apply
```

**Text File**: Any text for frequency counting
```
I need to apply for the job.
The apple was red.
```

## Notes

- Words are cleaned (lowercase, no special characters)
- Empty words are ignored
- If fewer than k words exist, returns all available words
