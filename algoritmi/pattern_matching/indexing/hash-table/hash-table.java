/*
    The hash-table indexing technique uses the sequence-preprocessing to analyze and obtain the pattern positions in the sequence
    This approach uses more memory than the KMP-search: by using the 3.2B sequence the compiler is likely to throw a std::bad_alloc error
    By comparing this process with the vector<pattern, pos> we can see a major improvement time-wise: for 10 mln character this approach needs 5 seconds meanwhile the vector approach needs 40 seconds
    Nonetheless this approach is less efficient than the KMP search but allows to search different patterns at the same time by using the same index. On the other hand, for every pattern the KMP search needs a new oracle.
*/

import java.io.*;
import java.util.*;
import java.util.stream.Stream;

public class HashTableIndexing {

    // function definition
    public static List<Integer> findPattern(String sequence, String pattern, Map<String, List<Integer>> table, int k) {
        // list with all positions
        List<Integer> positions = new ArrayList<>();

        // first k chars of the pattern
        String patternSubs = pattern.substring(0, k);

        // gets all the positions
        List<Integer> positionsUncheck = table.get(patternSubs);

        // checks for the rest of the pattern
        for (int pos : positionsUncheck) {
            if (checkPosition(pos, k, pattern, sequence)) {
                positions.add(pos);
            }
        }

        return positions;
    }

    // Checks if the position is the beginning of a pattern occurrence
    public static boolean checkPosition(int sequenceIdx, int k, String pattern, String sequence) {
        int startCheck = sequenceIdx + k;

        for (int i = 0; i < pattern.length() - k; i++) {
            // if reached the end of the string or the pattern does not match
            if (startCheck + i >= sequence.length() || sequence.charAt(startCheck + i) != pattern.charAt(k + i)) {
                return false;
            }
        }

        return true;
    }

    // Function to generate an index of substrings of length k from a given sequence
    // The hash_table maps substrings to the positions where they occur in the sequence
    public static Map<String, List<Integer>> getHashTable(String sequence, int k) {
        Map<String, List<Integer>> hashTable = new HashMap<>();
        String s;

        // Iterate through the sequence to create the index
        for (int i = 0; i <= sequence.length() - k; i++) {
            s = sequence.substring(i, i + k);
            hashTable.putIfAbsent(s, new ArrayList<>());
            hashTable.get(s).add(i);
        }

        return hashTable; // Return the generated hash table
    }

    public static void main(String[] args) {
        final String DATA_FOLDER_PATH = "../../../../data/";

        // defines the source files
        final String sequenceFile = "sequence-10000.txt";
        final String patternFile = "pattern-5.txt";
        final String outputFile = "hash-table-indexing-output.txt";

        final int k = 5;

        // uses files to address input and output
        try (BufferedReader inputSequence = new BufferedReader(new FileReader(DATA_FOLDER_PATH + sequenceFile));
             BufferedReader inputPattern = new BufferedReader(new FileReader(DATA_FOLDER_PATH + patternFile));
             PrintWriter output = new PrintWriter(new FileWriter(outputFile))) {

            // displays eventual errors
            if (inputSequence == null || inputPattern == null) {
                System.out.println("Error opening files.");
                return;
            }

            System.out.println("Files accepted.");

            // defines sequence and pattern
            String sequence = inputSequence.readLine();
            String pattern = inputPattern.readLine();

            long start = System.currentTimeMillis();

            // gets hash_table
            Map<String, List<Integer>> hashTable = getHashTable(sequence, k);

            Stream<Map.Entry<String, List<Integer>>> sorted = hashTable.entrySet().stream().sorted(Map.Entry.comparingByKey());
            sorted.forEach(System.out::println);

            // gets positions
            List<Integer> positions = findPattern(sequence, pattern, hashTable, k);

            long stop = System.currentTimeMillis();
            long duration = stop - start;

            // outputs duration and matches
            System.out.println("Hash-Table:");
            System.out.println("   Matches:    " + positions.size());
            System.out.println("   Duration:   " + duration + " ms");

            // writes the indexes
            output.println("Total occurrences: " + positions.size());
            for (int i : positions) {
                output.println(i);
            }

            // opens output file
            new ProcessBuilder("xdg-open", outputFile).inheritIO().start();

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
