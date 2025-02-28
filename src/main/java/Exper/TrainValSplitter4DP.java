/**
 * 为深度学习算法划分 训练集、验证集和测试集 = 6. 2. 2
 */
package Exper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TrainValSplitter4DP {

    public static void main(String[] args) {
        // 文件夹路径
        String sourceFolderPath = "preExperData/RTreeTokenContext1/"; // 输入文件夹路径
        String trainFolderPath = "preExperData/RTreeTokenContext1/train"; // 输出训练集文件夹路径
        String valFolderPath = "preExperData/RTreeTokenContext1/val"; // 输出验证集文件夹路径
        String testFolderPath = "preExperData/RTreeTokenContext1/test"; // 输出测试集文件夹路径

        // 划分比例
        double trainRatio = 0.6;
        double valRatio = 0.2;
        double testRatio = 0.2;

        // 创建训练集和验证集文件夹
        createFolder(trainFolderPath);
        createFolder(valFolderPath);
        createFolder(testFolderPath);

        // 获取源文件夹中的所有文件
        File sourceFolder = new File(sourceFolderPath);
        File[] files = sourceFolder.listFiles();

        if (files != null) {
            // 随机打乱文件列表
            List<File> fileList = new ArrayList<>(List.of(files));
//            Collections.shuffle(fileList, new Random());

            // 计算训练集和验证集的文件数量
            int trainSize = (int) (fileList.size() * trainRatio);
            int valSize = (int) (fileList.size() * valRatio);

            // 将文件分配到训练集和验证集文件夹
            for (int i = 0; i < fileList.size(); i++) {
                File file = fileList.get(i);
                try {
                    if (i < trainSize) {
                        // 移动到训练集文件夹
                        moveFile(file.toPath(), Paths.get(trainFolderPath, file.getName()));
                    } else if(i < valSize) {
                        // 移动到验证集文件夹
                        moveFile(file.toPath(), Paths.get(valFolderPath, file.getName()));
                    } else {
                        // 移动到测试集文件夹
                        moveFile(file.toPath(), Paths.get(testFolderPath, file.getName()));
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
    private static void moveFile(Path source, Path destination) throws IOException {
        Files.move(source, destination);
    }
}
