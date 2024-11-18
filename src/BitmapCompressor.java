/******************************************************************************
 *  Compilation:  javac BitmapCompressor.java
 *  Execution:    java BitmapCompressor - < input.bin   (compress)
 *  Execution:    java BitmapCompressor + < input.bin   (expand)
 *  Dependencies: BinaryIn.java BinaryOut.java
 *  Data files:   q32x48.bin
 *                q64x96.bin
 *                mystery.bin
 *
 *  Compress or expand binary input from standard input.
 *
 *  % java DumpBinary 0 < mystery.bin
 *  8000 bits
 *
 *  % java BitmapCompressor - < mystery.bin | java DumpBinary 0
 *  1240 bits
 ******************************************************************************/

/**
 *  The {@code BitmapCompressor} class provides static methods for compressing
 *  and expanding a binary bitmap input.
 *
 *  @author Robert Sedgewick
 *  @author Kevin Wayne
 *  @author Zach Blick
 *  @author SIERRA SHAW
 */
public class BitmapCompressor {

    /**
     * Reads a sequence of bits from standard input, compresses them,
     * and writes the results to standard output.
     */
    public static void compress() {
        String str = BinaryStdIn.readString();
        int strlen = str.length();
        int max_reps = 0;
        char current = str.charAt(0);
        int current_reps = 0;

        // Add in the MetaData how long the String is
        BinaryStdOut.write(strlen);

        // Add at first whether it starts at a 0 or 1 (1 bit)
        BinaryStdOut.write(current, 1);

        // Loop through String to see the longest instance of a 1 or a 0
        for (int i = 0; i < strlen; i++) {
            if (str.charAt(i) != current) {
                if (current_reps > max_reps) {
                    max_reps = current_reps;
                }
                current_reps = 1;
            }
            else {
                current += 1;
            }
        }

        // Find radix (bits needed to represent max_reps)
        int radix = (int) (Math.log(max_reps) / Math.log(2)) + 1;
        BinaryStdOut.write(radix);

        current = str.charAt(0);
        current_reps = 0;

        // Use the radix needed for the longest instance to write in data of constant length of a set of characters
        for (int i = 0; i < strlen; i++) {
            // Once the next one is the opposite, add the repeated instances number to the file in previously found radix
            if (str.charAt(i) != current) {
                BinaryStdOut.write(current_reps, radix);
                current_reps = 1;
            }
            // Count how many repeated instances of that given number
            else {
                current_reps += 1;
            }
        }

        BinaryStdOut.close();
    }

    /**
     * Reads a sequence of bits from standard input, decodes it,
     * and writes the results to standard output.
     */
    public static void expand() {

        // Get the MetaData
        int strlen = BinaryStdIn.readInt();
        char current_char = (char) BinaryStdIn.readInt(1);
        int radix = BinaryStdIn.readInt();

        for (int i = 0; i < strlen; i++) {
            // Only read in X bits, then write the current char out that read amount times
            int reps = BinaryStdIn.readInt(radix);
            for (int j = 0; j < reps; j++) {
                BinaryStdOut.write(current_char, radix);
            }
        }

        BinaryStdOut.close();
    }

    /**
     * When executed at the command-line, run {@code compress()} if the command-line
     * argument is "-" and {@code expand()} if it is "+".
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}