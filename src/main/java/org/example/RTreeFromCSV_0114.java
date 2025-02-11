package org.example;

import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class RTreeFromCSV_0114 {

    static int maxChildren = 8;

    public static void main(String[] args) {
        String directoryPath = "D:\\project\\Java\\rtree_construct\\splitByTime";  // 替换为您的文件夹路径
        File folder = new File(directoryPath);

        // 确保该路径是一个文件夹
        if (folder.isDirectory()) {
            // 遍历文件夹中的所有CSV文件
            for (File file : folder.listFiles()) {
                if (file.isFile() && file.getName().endsWith(".csv")) {
                    // 为每个CSV文件构建R-tree
                    buildRTreeFromCSV(file);
                }
            }
        } else {
            System.out.println("指定路径不是一个有效的文件夹");
        }
    }

    // 构建R-tree并保存为新的文件
    public static void buildRTreeFromCSV(File csvFile) {
        String line;
        String csvSplitBy = ",";
        RTree<String, Point> tree = RTree.maxChildren(maxChildren).create();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            // 读取CSV文件的标题行
            br.readLine(); // Skip the header

            // 逐行读取CSV文件中的数据
            while ((line = br.readLine()) != null) {
                // 使用逗号分隔符分割每一行数据
                String[] values = line.split(csvSplitBy);

                // 提取Local_X和Local_Y列的数据
                double Grid_X = Double.parseDouble(values[4]);
                double Grid_Y = Double.parseDouble(values[5]);

                // 使用Local_X和Local_Y作为坐标，添加到R*-tree中
                String vehicleID = values[0]; // 使用Vehicle_ID作为数据标识符
                tree = tree.add(vehicleID, Geometries.point(Grid_X, Grid_Y));
            }

            // 保存R*-tree的结构到文件
            saveRTreeToFile(csvFile.getName(), tree);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 将R-tree的结构保存到文件
    public static void saveRTreeToFile(String fileName, RTree<String, Point> tree) {
        String outputFilePath = "D:\\project\\Java\\rtree_construct\\output\\" + fileName.replace(".csv", "_rtree.txt");
        try (FileWriter writer = new FileWriter(outputFilePath)) {
            writer.write(tree.asString());
            System.out.println("R-tree已保存到文件: " + outputFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
