// Manhattan Tourist Problema: risolto con LCS (Longest Common Subsequence)

import java.util.*;

/*

matrice 3 x 3
         A         T         C
    . __ n __ . __ n __ . __ n __ .
    |         |         |         |
 G  n         n         n         n
    |         |         |         |
    . __ n __ . __ n __ . __ n __ .
    |         |         |         |
 T  n         n         n         n
    |         |         |         |
    . __ n __ . __ n __ . __ n __ .
    |         |         |         |
 C  n         n         n         n
    |         |         |         |
    . __ n __ . __ n __ . __ n __ .

*/

public class MTP{
    public static void main(String[]args){
        Scanner scanner = new Scanner(System.in);
        try{
            System.out.print("Inserisci il numero delle colonne:");
            int n_col = scanner.nextInt();
            System.out.print("Inserisci il numero delle righe:");
            int n_raw = scanner.nextInt();
            for(int i = 0; i < n_raw; i++){
                for(int j = 0; j < n_col; j++){
                    if(i==0 | i%4==0){
                        g
                    }
                }
            }
        }
        catch(Exception e){
            System.err.println("Error "+e);
        }
    }
}
