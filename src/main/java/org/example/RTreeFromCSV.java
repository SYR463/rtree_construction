package org.example;

import com.github.davidmoten.rtree.*;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Point;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RTreeFromCSV {

     static int maxChildren = 8;

    public static void main(String[] args) {
        String csvFile = "D:\\project\\python\\ARPN4ITS\\dataprocess\\data\\processed\\splitByGlobalTime\\data_1118846983500.csv";  // 替换为您的CSV文件路径
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

        // 输出R*-tree的结构
        System.out.println(tree.asString());

        System.out.println("-----------------------");

        // 将R*-tree转换为序列
//        List<String> sequence = serializeRTree(tree.root());
//        System.out.println(sequence);
    }

}
