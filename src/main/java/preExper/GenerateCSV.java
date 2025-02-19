package preExper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class GenerateCSV {
    public static void main(String[] args) {
        // 生成 10000 个Global_Time
        long[] globalTimes = new long[10000];
        for (int i = 0; i < 10000; i++) {
            globalTimes[i] = 1118846982100L + i * 100;  // 假设每个Global_Time相差100
        }

        // 叶节点数量
        int vehicleNum = 6;
        // M*N 网格边界
        int M = 4;
        int N = 5;

        // 指定保存CSV文件的文件夹路径
        String folderPath = "preExperData/generateData/";

        // 创建文件夹（如果不存在）
        File folder = new File(folderPath);
        if (!folder.exists()) {
            boolean folderCreated = folder.mkdirs();
            if (folderCreated) {
                System.out.println("文件夹已创建: " + folderPath);
            } else {
                System.out.println("文件夹创建失败！");
            }
        }

        // 对每个Global_Time生成单独的CSV文件
        for (long globalTime : globalTimes) {
            // 根据Global_Time生成文件名，并将文件保存在指定文件夹中
            String filePath = folderPath + File.separator + "output_" + globalTime + ".csv";

            try (FileWriter writer = new FileWriter(filePath)) {
                // 写入CSV头部
                writer.append("Vehicle_ID,Global_Time,Grid_X,Grid_Y,Min_X,Min_Y,Max_X,Max_Y\n");

                // 随机生成唯一的Grid_X 和 Grid_Y对
                Random random = new Random();
                boolean[][] gridUsed = new boolean[M][N];  // 记录哪些网格已经被使用


                // 每个Global_Time生成多个车辆记录
                for (int vehicleId = 1; vehicleId <= vehicleNum; vehicleId++) {  // 假设每个时间戳下有6辆车
                    // 确保Grid_X, Grid_Y唯一且不重复
                    int gridX, gridY;

                    do {
                        gridX = random.nextInt(M);  // 生成0到M-1之间的整数
                        gridY = random.nextInt(N);  // 生成0到N-1之间的整数
                    } while (gridUsed[gridX][gridY]);  // 如果这个坐标已经使用过，则重新生成

                    // 标记此网格已经被使用
                    gridUsed[gridX][gridY] = true;


                    // 写入车辆数据，Min_X, Min_Y, Max_X, Max_Y 使用符号占位
                    writer.append(String.format("%d,%d,%d,%d,B,4,C,5\n", vehicleId, globalTime, gridX, gridY));
                }

                System.out.println("CSV文件已成功生成: " + filePath);

            } catch (IOException e) {
                System.out.println("生成CSV文件时出错：" + e.getMessage());
            }
        }
    }
}
