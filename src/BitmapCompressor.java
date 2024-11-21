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

import java.util.ArrayList;

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
        ArrayList<Integer> data = new ArrayList<Integer>();
        // Reading in the bits, bit by bit
        while (!BinaryStdIn.isEmpty()) {
            data.add(BinaryStdIn.readInt(1));
        }
        int dataSize = data.size();

        // Assuming the file starts at 0
        int current = 0;
        int current_reps = 0;
        ArrayList<Integer> repsPerRun = new ArrayList<Integer>();

        // Loop through String to see the reps per run of a 0 or a 1
        for (int i = 0; i < dataSize; i++) {
            if (data.get(i) != current) {
                repsPerRun.add(current_reps);
                current_reps = 1;
                current = data.get(i);
            }
            else {
                current_reps += 1;
            }
        }
        // Add the last run to the ArrayList
        repsPerRun.add(current_reps);

        // Find radix and max (Sedgewick's way)
        int radix = 8;
        int max = (int) Math.pow(2, radix) - 1;

        // Use the radix needed for the longest instance to write in data of constant length of a set of characters
        for (int reps: repsPerRun) {
            // Fixing overflow (Sedgewick's way)
            while (reps > max) {
                BinaryStdOut.write(max, radix);
                BinaryStdOut.write(0, radix);
                reps -= max;
            }
            BinaryStdOut.write(reps % max, radix);
        }

        BinaryStdOut.close();
    }

    /**
     * Reads a sequence of bits from standard input, decodes it,
     * and writes the results to standard output.
     */
    public static void expand() {
        // No metadata with Sedgewick, assume the first bit is 0
        int currentBit = 0;
        int radix = 8;

        while (!BinaryStdIn.isEmpty()) {
            // Only read in 8 bits, then write the current char out that read amount times
            int reps = BinaryStdIn.readInt(radix);
            for (int j = 0; j < reps; j++) {
                BinaryStdOut.write(currentBit, 1);
            }
            // Switch to next bit
            currentBit = (currentBit + 1) % 2;
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
/*
public static void compress() {
        ArrayList<Integer> data = new ArrayList<Integer>();
        while (!BinaryStdIn.isEmpty()) {
            data.add(BinaryStdIn.readInt(1));
        }
        int dataSize = data.size();
        // See how many switches for 0 to 1
        int switches = 1;
        int max_reps = 0;
        int current = data.get(0);
        int current_reps = 0;

        // Metadata

        // Add at first whether it starts at a 0 or 1 (1 bit)
        BinaryStdOut.write(current, 1);

        // Loop through String to see the longest instance of a 1 or a 0
        for (int i = 0; i < dataSize; i++) {
            if (data.get(i) != current) {
                if (current_reps > max_reps) {
                    max_reps = current_reps;
                }
                current_reps = 1;
                current = data.get(i);
                switches += 1;
            }
            else {
                current_reps += 1;
            }
        }

        // Add in the MetaData how long the String is
        BinaryStdOut.write(switches);

        // Find radix (bits needed to represent max_reps)
        int radix = (int) (Math.log(max_reps) / Math.log(2)) + 1;
        BinaryStdOut.write(radix);

        current = data.get(0);
        current_reps = 0;

        // Use the radix needed for the longest instance to write in data of constant length of a set of characters
        for (int i = 0; i < dataSize; i++) {
            // Once the next one is the opposite, add the repeated instances number to the file in previously found radix
            if (data.get(i) != current) {
                BinaryStdOut.write(current_reps, radix);
                current_reps = 1;
                current = data.get(i);
            }
            // Count how many repeated instances of that given number
            else {
                current_reps += 1;
            }
        }
        // Print out the last section
        BinaryStdOut.write(current_reps, radix);

        BinaryStdOut.close();
    }

public static void expand() {
    // Get the MetaData
    int currentBit = BinaryStdIn.readInt(1);
    int switches = BinaryStdIn.readInt();
    int radix = BinaryStdIn.readInt();

    for (int i = 0; i < switches; i++) {
        // Only read in X bits, then write the current char out that read amount times
        int reps = BinaryStdIn.readInt(radix);
        for (int j = 0; j < reps; j++) {
            BinaryStdOut.write(currentBit, 1);
        }
        currentBit = (currentBit + 1) % 2;
    }
    BinaryStdOut.close();
}
*/