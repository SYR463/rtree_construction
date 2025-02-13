/**
 * 将 构建得到的 R-tree 序列结构，转化为token序列，并添加相应的<S>占位符
 *
 * "mbr=Rectangle [x1=1, y1=3, x2=4, y2=9]\n" +
 *  "  mbr=Rectangle [x1=1, y1=3, x2=4, y2=4]\n" +
 *  "    entry=Entry [value=8, geometry=Rectangle [x1=2, y1=3, x2=3, y2=4]]\n" +
 *  "  mbr=Rectangle [x1=3, y1=3, x2=4, y2=4]";
 *
 *  Token 序列: [<BEG>, <NL>, 1,3, 4,9, <NL>, 1,3, 4,4, <L>, 2,3, 3,4, <NL>, 3,3, 4,4, <END>]
 *
 *  TODO:
 *  添加占位符标记<S>
 *
 */

package ARPN4ITS;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class TokenizeRTree {

    // 假设的词汇表（映射坐标到 token）
    private static final Map<String, Integer> vocab = new HashMap<>();

    static {
        vocab.put("<L>", 0);   // 叶节点
        vocab.put("<NL>", 1);  // 非叶节点
        vocab.put("<BEG>", 2); // 起始标记
        vocab.put("<END>", 3); // 结束标记
        vocab.put("<S>", 4);   // 特殊符号

        // 生成 A0, A1, ... B0, B1, ...
        int index = 5;
        for (char letter = 'A'; letter <= 'Z'; letter++) {
            for (int num = 0; num < 10; num++) {
                vocab.put("" + letter + num, index++);
            }
        }
    }

    // 用于保存生成的 token 序列
    public static List<String> parseTreeStringToTokens(String treeString) {
        List<String> tokens = new ArrayList<>();
        String[] lines = treeString.split("\n");

        tokens.add("<BEG>");

        int[] IndentationLevelArray = new int[10];  // 缩进层级数组，记录当前不同层级中的条目个数
        int deepIndentationLevel = 0;  // 最大缩进层级，也表示树的高度
        int currentIndentationLevel = 0; // 当前缩进层级

        int lastIndentationLevel = 0;

        for (String line : lines) {
            // 获取当前行的缩进层级（计算当前行前方空格的长度）
            currentIndentationLevel = (line.length() - line.trim().length()) / 2;

            String trimmedLine = line.trim();
            String[] parts = trimmedLine.split("[\\[\\],=\\s]+");

            String minX = "";
            String minY = "";
            String maxX = "";
            String maxY = "";

            // 判断是否需要插入<S>占位符，
            // <S> 插入条件：在遇到 缩进层级减少 时（即 indentLevel < lastIndentationLevel），表示父节点的结束，此时插入 <S>。
            // 同时需要插入标签<NL>或<L>，则用currentIndentationLevel与deepIndentationLevel之差来进行判断
            if (currentIndentationLevel < lastIndentationLevel) {
                if(lastIndentationLevel == deepIndentationLevel) {
                    tokens.add("<L>");
                } else {
                    tokens.add("<NL>");
                }
                tokens.add("<S>");  // 插入占位符
            }

            if (trimmedLine.startsWith("mbr=Rectangle")) {
                tokens.add("<NL>");

                // 提取 MBR 坐标（如 x1=1, y1=2, x2=4, y2=17）
                minX = String.valueOf((int) Double.parseDouble(parts[3]));
                minY = String.valueOf((int) Double.parseDouble(parts[5]));
                maxX = String.valueOf((int) Double.parseDouble(parts[7]));
                maxY = String.valueOf((int) Double.parseDouble(parts[9]));

                // 更新当前行的条目数量
                IndentationLevelArray[currentIndentationLevel]++;
            }

            if (trimmedLine.startsWith("entry=Entry")) {
                tokens.add("<L>");

                // 提取 MBR 坐标（如 x1=1, y1=2, x2=4, y2=17）
                minX = String.valueOf((int) Double.parseDouble(parts[7]));
                minY = String.valueOf((int) Double.parseDouble(parts[9]));
                maxX = String.valueOf((int) Double.parseDouble(parts[11]));
                maxY = String.valueOf((int) Double.parseDouble(parts[13]));

                // 更新当前行的条目数量，以及当前分支的最大层次
                IndentationLevelArray[currentIndentationLevel]++;
                deepIndentationLevel = currentIndentationLevel;
            }

            // 这里将坐标转化为整数并添加 token
            minX = convertToLetter(Integer.parseInt(minX));
            maxX = convertToLetter(Integer.parseInt(maxX));
            tokens.add(minX + minY);  // 格式如 "A1"
            tokens.add(maxX + maxY);  // 格式如 "B2"


            // 更新缩进层级
            lastIndentationLevel = currentIndentationLevel;

        }

        // 添加结束标记
        tokens.add("<END>");
        return tokens;
    }

    // 转换数字为大写字母
    public static String convertToLetter(int number) {
        // 假设 number 范围为 0-25
        return String.valueOf((char) ('A' + number));
    }


    // 读取文件夹中的所有文件，并对每个文件进行处理
    public static void processFilesInDirectory(String inputFolder, String outputFolder) throws IOException {
        // 创建输出文件夹，如果不存在
        File outputDir = new File(outputFolder);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        // 获取目录中的所有 .txt 文件
        File folder = new File(inputFolder);
        File[] files = folder.listFiles((dir1, name) -> name.endsWith(".txt"));

        if (files == null || files.length == 0) {
            System.out.println("No .txt files found in the directory.");
            return;
        }

        // 遍历每个文件并处理
        for (File file : files) {
            System.out.println("Processing file: " + file.getName());

            // 读取文件内容
            String treeString = new String(Files.readAllBytes(file.toPath()));

            // 获取 token 序列
            List<String> tokenSequence = parseTreeStringToTokens(treeString);

            // 输出 token 序列到新文件
            String outputFileName = outputFolder + File.separator + "tokens_" + file.getName();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName))) {
                for (String token : tokenSequence) {
                    writer.write(token + " ");
                }
                System.out.println("Token sequence saved to: " + outputFileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main1(String[] args) {
        String treeString = "mbr=Rectangle [x1=1, y1=3, x2=4, y2=9]\n" +
                "  mbr=Rectangle [x1=1, y1=3, x2=4, y2=4]\n" +
                "    mbr=Rectangle [x1=1, y1=3, x2=4, y2=4]\n" +
                "      entry=Entry [value=8, geometry=Rectangle [x1=2, y1=3, x2=3, y2=4]]\n" +
                "  mbr=Rectangle [x1=3, y1=3, x2=4, y2=4]\n" +
                "    entry=Entry [value=14, geometry=Rectangle [x1=3, y1=3, x2=4, y2=4]]\n";

        List<String> tokenSequence = parseTreeStringToTokens(treeString);
        System.out.println("Token 序列: " + tokenSequence);
    }

    public static void main(String[] args) {
        try {
            // 传入文件夹路径，处理该文件夹中的所有 .txt 文件
            String inputFolder = "D:\\project\\python\\ARPN4ITS\\dataPreprocess\\outputRTree\\";
            String outputFolder = "D:\\project\\python\\ARPN4ITS\\dataPreprocess\\outputSequenceRTree\\";  // 输出R*-tree的结构

            processFilesInDirectory(inputFolder, outputFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
