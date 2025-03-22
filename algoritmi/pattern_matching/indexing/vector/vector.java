/*
    La tecnica di indicizzazione vector<pattern, position> utilizza il preprocessing della sequenza per analizzare e ottenere le posizioni dei pattern nella sequenza.
    Questo approccio utilizza più memoria rispetto alla ricerca KMP ed è estremamente meno efficiente in termini di tempo, anche se il vettore è ordinato e viene utilizzato l'algoritmo di ricerca binaria.
    Per cercare in una sequenza di 10 milioni di caratteri, il tempo necessario è di circa 40 secondi (con una lunghezza del pattern di 10).
*/

import java.io.*;
import java.util.*;


public class Main {
    static class Pair<K, V> {
        private K key;
        private V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }
    }
    // definizione della funzione
    public static List<Integer> findPattern(String sequence, String pattern, List<Pair<String, Integer>> v, int k) {
        // vettore con tutte le posizioni
        List<Integer> positions = new ArrayList<>();

        // primi k caratteri del pattern
        String patternSubs = pattern.substring(0, k);

        // il vettore conterrà tutte le posizioni non controllate
        List<Integer> positionsUncheck = new ArrayList<>();

        // trova le occorrenze utilizzando una ricerca binaria
        int lowIndex = Collections.binarySearch(v, new Pair<>(patternSubs, 0), Comparator.comparing(Pair::getKey));
        if (lowIndex >= 0) {
            positionsUncheck.add(v.get(lowIndex).getValue());
            int highIndex = lowIndex + 1;

            // aggiunge le posizioni possibili
            while (lowIndex >= 0 && v.get(lowIndex).getKey().equals(patternSubs)) {
                positionsUncheck.add(v.get(lowIndex).getValue());
                lowIndex--;
            }

            while (highIndex < v.size() && v.get(highIndex).getKey().equals(patternSubs)) {
                positionsUncheck.add(v.get(highIndex).getValue());
                highIndex++;
            }
        }

        // controlla per il pattern effettivo, caso in cui |k| < |pattern|
        for (int pos : positionsUncheck) {
            boolean flag = true;
            int startCheck = pos + k;

            for (int i = 0; i < pattern.length() - k; i++) {
                // se si raggiunge la fine della stringa o il pattern non corrisponde
                if (startCheck + i >= sequence.length() || sequence.charAt(startCheck + i) != pattern.charAt(k + i)) {
                    flag = false;
                    break;
                }
            }

            if (flag) {
                positions.add(pos);
            }
        }

        return positions;
    }

    // Funzione per generare un indice di sottostringhe di lunghezza k da una sequenza data
    public static List<Pair<String, Integer>> getSortedVector(String sequence, int k) {
        List<Pair<String, Integer>> v = new ArrayList<>();

        // popola l'array non ordinato
        for (int i = 0; i <= sequence.length() - k; i++) {
            v.add(new Pair<>(sequence.substring(i, i + k), i));
        }

        v.sort(Comparator.comparing(Pair::getKey));
        return v;
    }

    public static void main(String[] args) {
        final String DATA_FOLDER_PATH = "../../../../data/";

        // definisce i file sorgente
        String sequenceFile = "sequence-10000.txt";
        String patternFile = "pattern-5.txt";
        String outputFile = "vector-indexing-output.txt";

        final int k = 5;

        // utilizza i file per indirizzare input e output
        try (BufferedReader inputSequence = new BufferedReader(new FileReader(DATA_FOLDER_PATH + sequenceFile));
             BufferedReader inputPattern = new BufferedReader(new FileReader(DATA_FOLDER_PATH + patternFile));
             PrintWriter output = new PrintWriter(new FileWriter(outputFile))) {

            // definisce sequenza e pattern
            String sequence = inputSequence.readLine();
            String pattern = inputPattern.readLine();

            long start = System.nanoTime();

            // ottiene il vettore
            List<Pair<String, Integer>> v = getSortedVector(sequence, k);

            // ottiene le posizioni
            List<Integer> positions = findPattern(sequence, pattern, v, k);

            long stop = System.nanoTime();
            long duration = (stop - start) / 1_000_000; // in millisecondi

            // output durata e corrispondenze
            System.out.println("\nVector:");
            System.out.println("   Matches:    " + positions.size());
            System.out.println("   Duration:   " + duration + " ms\n");

            // scrive gli indici
            output.println("Total occurrences: " + positions.size() + "\n");
            for (int i : positions) {
                output.println(i);
            }

            // apre il file di output
            new ProcessBuilder("xdg-open", outputFile).inheritIO().start();

        } catch (IOException e) {
            System.err.println("Errore nell'aprire i file.");
            e.printStackTrace();
        }
    }
}
