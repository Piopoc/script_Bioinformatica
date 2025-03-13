// KMP Algorithm: ricerca di un pattern all'interno di un testo

import java.util.ArrayList;
import java.util.Arrays;

public class KMP_algorithm {

    public static void constructLps(String pat, int[] lps) {

        int len = 0;

        lps[0] = 0;

        int i = 1;
        while (i < pat.length()) {
            // Se i caratteri coincidono, incremento la dimensione dell'array lps
            if (pat.charAt(i) == pat.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            }
            // Se i caratteri non coincidono
            else {
                if (len != 0) {
                    // Aggiorno la lunghezza al valore precedente di lps
                    len = lps[len - 1];
                }
                else {
                    // Se non coincide con il prefisso trovato, lps[i]=0
                    lps[i] = 0;
                    i++;
                }
            }
        }
    }

   public static ArrayList<Integer> search(String pat, String txt) {
        int n = txt.length();
        int m = pat.length();

        int[] lps = new int[m];
        ArrayList<Integer> res = new ArrayList<>();

        constructLps(pat, lps);
        System.out.println("LPS: " + Arrays.toString(lps));

        // "puntatori" i e j per l'attraversamento
        int i = 0; // relativo al testo
        int j = 0; // relativo al pattern

        while (i < n) {
            // Se i caratteri coincidono, sposto i puntatori di una cella avanti ciascuno
            if (txt.charAt(i) == pat.charAt(j)) {
                // Se l'intero pattern coincide, salvo l'indice iniziale in result
                if (j == m-1) {
                    res.add(i-j);
                    // ORACOLO: mi sposto tanto indietro quanto è necessario
                    j = lps[j];
                }
                else {
                    i++;
                    j++;
                }
            }
            // Se i caratteri non coincidono
            else {
                // ORACOLO: mi sposto tanto indietro quanto è necessario
                if (j != 0){
                    j = lps[j - 1];
                }
                else{
                    i++;
                }
            }
        }
        return res;
    }

    public static void main(String[] args) {
        String txt = "aabaacaadaabaaba".toUpperCase();
        String pat = "aaba".toUpperCase();

        System.out.println("Testo: "+txt);
        System.out.println("Pattern: "+pat);
        ArrayList<Integer> res = search(pat, txt);
        System.out.print("Ricorrenze del pattern all'interno del testo: ");
        for (int i = 0; i < res.size(); i++){
            System.out.print(res.get(i) + " ");
        }
    }
}
