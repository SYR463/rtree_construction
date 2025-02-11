package org.example;

import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;

import java.io.*;
import java.util.*;

public class CSVToRTreeLabels {

    static int maxChildren = 8;

    public static void main(String[] args) throws IOException {
//        // Path to the folder containing CSV files
//        String folderPath = "path/to/folder";
//        String outputFolderPath = "path/to/output/folder";

//        String csvPath = "D:\\project\\python\\ARPN4ITS\\dataprocess\\data\\processed\\splitByGlobalTime\\data_1118846980900.csv";
        String folderPath = "D:\\project\\python\\ARPN4ITS\\dataprocess\\data\\processed\\splitByGlobalTime";
        String outputFolderPath = "D:\\project\\python\\ARPN4ITS\\dataprocess\\data\\processed\\splitByGlobalTimeLabel";

        // Process all files in the folder
        processFolder(folderPath, outputFolderPath);
    }

    public static void processFolder(String folderPath, String outputFolderPath) throws IOException {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();

        if (files == null || files.length == 0) {
            System.out.println("No files found in the folder.");
            return;
        }

        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".csv")) {
                String inputFilePath = file.getAbsolutePath();
                String outputFilePath = outputFolderPath + File.separator + file.getName().replace(".csv", "_labels.txt");

                System.out.println("Processing: " + inputFilePath);
                generateRTreeLabels(inputFilePath, outputFilePath);
                System.out.println("Output saved to: " + outputFilePath);
            }
        }
    }

    public static void generateRTreeLabels(String csvPath, String labelPath) throws IOException {
        // Read CSV data and construct RTree
        String rTreeString = readCSV(csvPath);

        // Convert to full binary tree sequence
        List<String> sequence = convertStringToSequence(rTreeString, maxChildren);

        // Write labels to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(labelPath))) {
            for (String label : sequence) {
                writer.write(label);
                writer.newLine();
            }
        }
    }


    // Reads a CSV file and returns a list of rows as maps
    private static String readCSV(String csvFile) throws IOException {
        String line;
        String csvSplitBy = ",";

        RTree<String, Point> tree = RTree.maxChildren(maxChildren).create();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            // 读取CSV文件的标题行
            line = br.readLine(); // Skip the header

            // 逐行读取CSV文件中的数据
            while ((line = br.readLine()) != null) {
                // 使用逗号分隔符分割每一行数据
                String[] values = line.split(csvSplitBy);

                // 提取Local_X和Local_Y列的数据
                double localX = Double.parseDouble(values[4]);
                double localY = Double.parseDouble(values[5]);

                // 使用Local_X和Local_Y作为坐标，添加到R*-tree中
                String vehicleID = values[0]; // 使用Vehicle_ID作为数据标识符
                tree = tree.add(vehicleID, Geometries.point(localX, localY));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return tree.asString();
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