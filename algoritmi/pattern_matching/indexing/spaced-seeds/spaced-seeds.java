import java.io.*;
import java.util.*;

public class Main {

    // Funzione principale
    public static void main(String[] args) {
        final String DATA_FOLDER_PATH = "../../../../data/";

        // Definisce i file sorgente
        final String sequence_file = "sequence-10000.txt";
        final String pattern_file = "pattern-5.txt";
        final String output_file = "spaced-seeds-indexing-output.txt";

        final String mask = "10001";

        // Usa i file per gestire input e output
        try (BufferedReader input_sequence = new BufferedReader(new FileReader(DATA_FOLDER_PATH + sequence_file));
             BufferedReader input_pattern = new BufferedReader(new FileReader(DATA_FOLDER_PATH + pattern_file));
             PrintWriter output = new PrintWriter(new FileWriter(output_file))) {

            // Visualizza eventuali errori
            if (input_sequence == null || input_pattern == null || output == null) {
                System.out.println("\n          \033[31m" + "\033[1m" + "Errore" + "\033[0m" + "\033[31m" + " nell'apertura dei file." + "\033[37m" + "\n");
                return;
            }

            System.out.println("\n            \033[32m" + "File " + "\033[1m" + "accettati" + "\033[0m\033[32m" + "." + "\033[37m" + "\n");

            // Definisce sequenza e pattern
            String sequence = input_sequence.readLine();
            String pattern = input_pattern.readLine();

            if (pattern.length() != mask.length()) {
                System.out.println("\n          \033[31m" + "Il pattern e la maschera devono avere la stessa dimensione." + "\033[37m" + "\n");
                return;
            }

            long start = System.nanoTime();

            // Ottiene la hash_table
            Map<String, List<Integer>> hash_table = get_hash_table(sequence, mask);

            // Ottiene le posizioni
            List<Integer> positions = find_pattern(pattern, hash_table, mask);

            long stop = System.nanoTime();
            long duration = (stop - start) / 1_000_000; // Converti in millisecondi

            // Output durata e corrispondenze
            System.out.println("\nSpaced seeds:");
            System.out.println("   Corrispondenze:    " + "\033[1m \033[33m" + positions.size() + "\033[37m \033[0m");
            System.out.println("   Durata:   " + "\033[34m" + duration + " ms" + "\033[37m" + "\n");

            // Scrive gli indici
            output.println("Occorrenze totali: " + positions.size());
            output.println();
            for (int i : positions)
                output.println(i);

        } catch (IOException e) {
            System.out.println("\n          \033[31m" + "Errore nell'apertura dei file." + "\033[37m" + "\n");
        }

        // Apre il file di output
        try {
            new ProcessBuilder("xdg-open", output_file).inheritIO().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    } // main

    // Questa funzione restituisce tutte le posizioni del pattern mascherato analizzando la hash_table
    public static List<Integer> find_pattern(String pattern, Map<String, List<Integer>> hash_table, String mask) {
        return hash_table.get(get_seed(pattern, mask));
    } // find_pattern

    // Crea e indicizza memorizzando tutti i k-mers mascherati
    public static Map<String, List<Integer>> get_hash_table(String sequence, String mask) {
        Map<String, List<Integer>> hash_table = new HashMap<>();
        String s;

        // Itera attraverso la sequenza per creare l'indice
        for (int i = 0; i <= sequence.length() - mask.length(); i++) {
            s = sequence.substring(i, i + mask.length());
            s = get_seed(s, mask);

            hash_table.computeIfAbsent(s, k -> new ArrayList<>()).add(i);
        }

        return hash_table;
    } // get_hash_table

    // Maschera il kmer
    public static String get_seed(String kmer, String mask) {
        StringBuilder spaced_seed = new StringBuilder();

        for (int i = 0; i < mask.length(); i++)
            if (mask.charAt(i) == '1')
                spaced_seed.append(kmer.charAt(i));

        return spaced_seed.toString();
    } // get_seed
}
