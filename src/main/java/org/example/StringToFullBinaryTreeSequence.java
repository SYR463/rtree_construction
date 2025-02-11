package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class StringToFullBinaryTreeSequence {

    public static void main(String[] args) {
        // Input string simulating R-tree toString output
        String rTreeString = "mbr=Rectangle [x1=4.161, y1=38.645, x2=51.191, y2=182.146]\n" +
                "  mbr=Rectangle [x1=27.399, y1=38.645, x2=51.191, y2=182.146]\n" +
                "    entry=Entry [value=18, geometry=Point [x=51.191, y=38.645]]\n" +
                "    entry=Entry [value=14, geometry=Point [x=49.016, y=104.894]]\n" +
                "    entry=Entry [value=8, geometry=Point [x=36.86, y=114.34]]\n" +
                "    entry=Entry [value=9, geometry=Point [x=27.399, y=45.993]]\n" +
                "    entry=Entry [value=5, geometry=Point [x=39.74, y=182.146]]\n" +
                "  mbr=Rectangle [x1=4.161, y1=62.943, x2=16.201, y2=160.379]\n" +
                "    entry=Entry [value=2, geometry=Point [x=16.064, y=160.379]]\n" +
                "    entry=Entry [value=13, geometry=Point [x=16.201, y=121.563]]\n" +
                "    entry=Entry [value=22, geometry=Point [x=14.953, y=62.943]]\n" +
                "    entry=Entry [value=10, geometry=Point [x=4.161, y=64.923]]";

        System.out.println(rTreeString);

        System.out.println("---------------------------");

        // Convert string to full binary tree sequence
        List<String> sequence = convertStringToSequence(rTreeString, 8); // Assume each node has a max capacity of 4
        System.out.println(sequence);
    }

    // Convert R-tree string output to full binary tree sequence
    public static List<String> convertStringToSequence(String rTreeString, int maxCapacity) {
        List<String> sequence = new ArrayList<>();
        sequence.add("\u27E8BEG\u27E9"); // Sequence start tag

        Scanner scanner = new Scanner(rTreeString);
        int currentSCount = 0;
        int previousIndentation = -1;

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            int currentIndentation = countLeadingSpaces(line);

            line = line.trim(); // Trim leading and trailing spaces

            if (line.startsWith("mbr=")) {
                // If current indentation is less than previous indentation, add remaining <S> tags
                if (previousIndentation >= 0 && currentIndentation <= previousIndentation) {
                    while (currentSCount > 0) {
                        sequence.add("\u27E8S\u27E9");
                        currentSCount--;
                    }
                }

                // Process non-leaf node (MBR)
                sequence.add("\u27E8NL\u27E9");
                sequence.add(line); // Add MBR information
                currentSCount = maxCapacity;
            } else if (line.startsWith("entry=")) {
                // Process leaf node (Entry)
                sequence.add("\u27E8L\u27E9");
                sequence.add(line); // Add entry information
                currentSCount--;
            }

            previousIndentation = currentIndentation;
        }

        // Add remaining <S> placeholders
        while (currentSCount > 0) {
            sequence.add("\u27E8S\u27E9");
            currentSCount--;
        }

        sequence.add("\u27E8END\u27E9"); // Sequence <end> tag
        return sequence;
    }

    // Count the number of leading spaces in the string
    private static int countLeadingSpaces(String line) {
        int count = 0;
        while (count < line.length() && line.charAt(count) == ' ') {
            count++;
        }
        return count;
    }
}
