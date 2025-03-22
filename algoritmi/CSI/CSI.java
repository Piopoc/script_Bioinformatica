import java.util.*;
import java.io.File;

public class CSI{

    public static int checkPat(String txt, String pat){
        int i = 0, max_rel = 0, max_eff = 0;
        while(i < txt.length()){
            if(txt.startsWith(pat,i)){
                max_rel++;
                i+=pat.length();
            }
            else{
                max_eff = Math.max(max_eff,max_rel);
                max_rel = 0;
                i++;
            }
        }
        return Math.max(max_eff,max_rel);
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Fornisci il file dei profili degli indagati e il file con la sequenza di DNA da controllare.");
            return;
        }

        File profilesFile = new File(args[0]);
        File dnaFile = new File(args[1]);
        List<String> strPatterns = new ArrayList<>();
        Map<String, List<Integer>> profiles = new HashMap<>();

        try (Scanner scanner = new Scanner(profilesFile)) {
            String header = scanner.nextLine();
            String[] headers = header.split(" ");
            for (int i = 1; i < headers.length; i++) {
                strPatterns.add(headers[i]);
            }
            System.out.println("STR: "+strPatterns);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] data = line.split(" ");
                String name = data[0];
                List<Integer> counts = new ArrayList<>();
                for (int i = 1; i < data.length; i++) {
                    counts.add(Integer.parseInt(data[i]));
                }
                profiles.put(name, counts);
            }
        } catch (Exception e) {
            System.err.println("Errore nella lettura del file dei profili: " + e.getMessage());
            return;
        }

        System.out.println(profiles);

        StringBuilder dnaSequence = new StringBuilder();
        try (Scanner scanner = new Scanner(dnaFile)) {
            while (scanner.hasNextLine()) {
                dnaSequence.append(scanner.nextLine());
            }
        } catch (Exception e) {
            System.err.println("Errore nella lettura del file DNA: " + e.getMessage());
            return;
        }
        System.out.println(dnaSequence);

        List<Integer> strCounts = new ArrayList<>();
        for (String str : strPatterns) {
            strCounts.add(checkPat(dnaSequence.toString(), str));
        }
        System.out.println(strCounts);

        for (Map.Entry<String, List<Integer>> entry : profiles.entrySet()) {
            String name = entry.getKey();
            List<Integer> counts = entry.getValue();
            if (counts.equals(strCounts)) {
                System.out.println(name);
                return;
            }
        }

        System.out.println("Nessun match trovato");
    }
}

// caso3 -> Charlie

