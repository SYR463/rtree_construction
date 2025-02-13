/**
 * 从CSV文件中读取车辆信息
 * Vehicle_ID,Global_Time,Grid_X,Grid_Y,Min_X,Min_Y,Max_X,Max_Y
 * 13,1118846982100,1,4,B,4,C,5
 * 2,1118846982100,1,7,B,7,C,8
 * 5,1118846982100,2,8,C,8,D,9
 * 14,1118846982100,3,3,D,3,E,4
 * 8,1118846982100,2,3,C,3,D,4
 *
 * 先对CSV文件中的车辆进行排序 (Grid_X, Grid_Y) ==》 x指车道（列）， y指划分的横向网格（行）
 * 因此，按照 先行后列，即先Grid_Y后Grid_X的方式排序
 *
 * 构建R*-tree，直接作为 矩形框插入，取 左下坐标 和 右上坐标
 *
 * mbr=Rectangle [x1=1.0, y1=3.0, x2=4.0, y2=9.0]
 *   entry=Entry [value=8, geometry=Rectangle [x1=2.0, y1=3.0, x2=3.0, y2=4.0]]
 *   entry=Entry [value=14, geometry=Rectangle [x1=3.0, y1=3.0, x2=4.0, y2=4.0]]
 *   entry=Entry [value=13, geometry=Rectangle [x1=1.0, y1=4.0, x2=2.0, y2=5.0]]
 *   entry=Entry [value=2, geometry=Rectangle [x1=1.0, y1=7.0, x2=2.0, y2=8.0]]
 *   entry=Entry [value=5, geometry=Rectangle [x1=2.0, y1=8.0, x2=3.0, y2=9.0]]
 */

package ARPN4ITS;

import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.SplitterRStar;
import com.github.davidmoten.rtree.Visualizer;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.github.davidmoten.rtree.geometry.Geometries;
//import io.reactivex.Observable;
import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RTreeFromFile {
    public static void main(String[] args) {
        // 输入文件夹路径（批量读取CSV文件）
//        String inputFolder = "D:\\project\\Java\\rtree_construct\\processedData\\";
//        String outputFolder = "D:\\project\\Java\\rtree_construct\\outputRTree\\";  // 输出R*-tree的结构
        String inputFolder = "D:\\project\\python\\ARPN4ITS\\dataPreprocess\\processedData\\";
        String outputFolder = "D:\\project\\python\\ARPN4ITS\\dataPreprocess\\outputRTree\\";  // 输出R*-tree的结构

        // 创建输出文件夹，如果不存在
        File outputDir = new File(outputFolder);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        // 获取文件夹中的所有CSV文件
        File folder = new File(inputFolder);
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".csv"));

        // 创建 R*-tree 的分割器
        SplitterRStar splitterRStar = new SplitterRStar();

        for (File csvFile : files) {
            System.out.println("处理文件: " + csvFile.getName());

            // 创建 R*-tree
            RTree<String, Rectangle> tree = RTree.minChildren(2).maxChildren(8).splitter(splitterRStar).create();

            // 读取 CSV 文件并批量插入
            List<String[]> dataList = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                String line;
                boolean headerSkipped = false;

                while ((line = br.readLine()) != null) {
                    if (!headerSkipped) {
                        headerSkipped = true;  // 跳过标题行
                        continue;
                    }

                    String[] values = line.split(",");
                    if (values.length < 8) continue; // 确保数据完整

                    dataList.add(values);  // 添加到列表
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // **按照 Grid_X 和 Grid_Y 排序，以车辆的移动方向为基准，按照从左到右的方式遍历**
            dataList = dataList.stream()
                    .sorted(Comparator.comparingInt((String[] row) -> Integer.parseInt(row[3])) // 按 Grid_Y 排序
                            .thenComparingInt(row -> Integer.parseInt(row[2]))) // Grid_Y 相同时按 Grid_X 排序
                    .collect(Collectors.toList());

            // **按排序后的数据插入 R-tree**
            for (String[] values : dataList) {
                String vehicleId = values[0];  // 车辆 ID
                double minX = Double.parseDouble(values[2]); // Min_X
                double minY = Double.parseDouble(values[3]); // Min_Y
                double maxX = Double.parseDouble(values[2]) + 1; // Max_X
                double maxY = Double.parseDouble(values[3]) + 1; // Max_Y

                // 插入数据到 R-tree
                tree = tree.add(vehicleId, Geometries.rectangle(minX, minY, maxX, maxY));
            }

//            RTreeProxy<String, Rectangle> proxy = new RTreeProxy<>(tree); // 创建代理对象

            // 将 R*-tree 结构输出到文件
            String outputFileName = outputFolder + csvFile.getName().replace(".csv", "_RTree.txt");
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName))) {
                writer.write(tree.asString());
//                writer.write(proxy.asString());
                System.out.println("R-tree 结构已保存到: " + outputFileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
