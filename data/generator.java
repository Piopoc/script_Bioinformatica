import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Main {

    // function definition
    public static void generate(long m, String fileName) {
        long start = System.currentTimeMillis();

        try (FileWriter output = new FileWriter(fileName)) {
            Random rand = new Random();
            while (m-- > 0) {
                switch (rand.nextInt(4) + 1) {
                    case 1:
                        output.write("A");
                        break;
                    case 2:
                        output.write("C");
                        break;
                    case 3:
                        output.write("G");
                        break;
                    case 4:
                        output.write("T");
                        break;
                }
            }
            output.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        long duration = System.currentTimeMillis() - start;

        // outputs the creations stats
        String spaces = "";
        for (int i = fileName.length(); i < 35; i++)
            spaces += " ";

        System.out.println(fileName + spaces + duration);
    }

    public static void main(String[] args) {
        /*
            Convention for file name:
                "type-length.extension"
        */

        long length;
        String type, extension, fileName;

        /* --- PATTERN --- */

        // generates a pattern of 5 characters
        length = 5;
        type = "pattern";
        extension = ".txt";
        fileName = type + "-" + length + extension;
        generate(length, fileName);

        /*

        // generates a pattern of 10 characters
        length = 10;
        type = "pattern";
        extension = ".txt";
        fileName = type + "-" + length + extension;
        generate(length, fileName);

        // generates a pattern of 20 characters
        length = 20;
        type = "pattern";
        extension = ".txt";
        fileName = type + "-" + length + extension;
        generate(length, fileName);

        // generates a pattern of 30 characters
        length = 30;
        type = "pattern";
        extension = ".txt";
        fileName = type + "-" + length + extension;
        generate(length, fileName);

        // generates a pattern of 50 characters
        length = 50;
        type = "pattern";
        extension = ".txt";
        fileName = type + "-" + length + extension;
        generate(length, fileName);

        // generates a pattern of 100 characters
        length = 100;
        type = "pattern";
        extension = ".txt";
        fileName = type + "-" + length + extension;
        generate(length, fileName);

        */

        /* --- SEQUENCE --- */

        // generates a sequence of 10k characters
        length = 10000;
        type = "sequence";
        extension = ".txt";
        fileName = type + "-" + length + extension;
        generate(length, fileName);

        /*
        // generates a sequence of 100k characters
        length = 100000;
        type = "sequence";
        extension = ".txt";
        fileName = type + "-" + length + extension;
        generate(length, fileName);

        // generates a sequence of 1M characters
        length = 1000000;
        type = "sequence";
        extension = ".txt";
        fileName = type + "-" + length + extension;
        generate(length, fileName);

        // generates a sequence of 10M characters
        length = 10000000;
        type = "sequence";
        extension = ".txt";
        fileName = type + "-" + length + extension;
        generate(length, fileName);

        // generates a sequence of 100M characters
        length = 100000000;
        type = "sequence";
        extension = ".txt";
        fileName = type + "-" + length + extension;
        generate(length, fileName);

        // generates a sequence of 1B characters
        length = 1000000000;
        type = "sequence";
        extension = ".txt";
        fileName = type + "-" + length + extension;
        generate(length, fileName);

        // generates a sequence of 3.2B characters
        length = 3200000000L;
        type = "sequence";
        extension = ".txt";
        fileName = type + "-" + length + extension;
        generate(length, fileName);

        */
    }
}
