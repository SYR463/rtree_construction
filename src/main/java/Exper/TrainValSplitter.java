package Exper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TrainValSplitter {

    public static void main(String[] args) {
        // 文件夹路径
        String sourceFolderPath = "ExperData/04_RTreeTokenContext/"; // 输入文件夹路径
        String trainFolderPath  = "ExperData/05_RTreeTokenContext/train"; // 输出训练集文件夹路径
        String valFolderPath    = "ExperData/06_RTreeTokenContext/val"; // 输出验证集文件夹路径

        // 划分比例
        double trainRatio = 0.7;

        // 创建训练集和验证集文件夹
        createFolder(trainFolderPath);
        createFolder(valFolderPath);

        // 获取源文件夹中的所有文件
        File sourceFolder = new File(sourceFolderPath);
        File[] files = sourceFolder.listFiles();

        if (files != null) {
            // 随机打乱文件列表
            List<File> fileList = new ArrayList<>(List.of(files));
            Collections.shuffle(fileList, new Random());

            // 计算训练集和验证集的文件数量
            int trainSize = (int) (fileList.size() * trainRatio);

            // 将文件分配到训练集和验证集文件夹
            for (int i = 0; i < fileList.size(); i++) {
                File file = fileList.get(i);
                try {
                    if (i < trainSize) {
                        // 复制到训练集文件夹
                        copyFile(file.toPath(), Paths.get(trainFolderPath, file.getName()));
                    } else {
                        // 复制到验证集文件夹
                        copyFile(file.toPath(), Paths.get(valFolderPath, file.getName()));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 创建文件夹，如果不存在的话
    private static void createFolder(String folderPath) {
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    // 移动文件
    private static void copyFile(Path source, Path destination) throws IOException {
        Files.copy(source, destination);
    }
}
