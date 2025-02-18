package preExper;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenConverter {

    // 映射函数：将x坐标转化为字母
    public static String mapXToLetter(double x) {
        return String.valueOf((char) ('A' + (int) x));  // 'A'的ASCII值是65
    }

    // 生成token的函数，输入坐标信息并转化为token
    public static String generateTokenFromRect(String rectString) {
        // 正则表达式匹配 Rectangle 的坐标
        Pattern pattern = Pattern.compile("x1=([\\d.]+), y1=([\\d.]+), x2=([\\d.]+), y2=([\\d.]+)");
        Matcher matcher = pattern.matcher(rectString);

        if (matcher.find()) {
            // 获取匹配的四个坐标
            double x1 = Double.parseDouble(matcher.group(1));
            double y1 = Double.parseDouble(matcher.group(2));
            double x2 = Double.parseDouble(matcher.group(3));
            double y2 = Double.parseDouble(matcher.group(4));

            // 转换坐标为token形式
            String token1 = mapXToLetter(x1) + (int) y1;
            String token2 = mapXToLetter(x2) + (int) y2;

            return token1 + token2;  // 返回token，如 "A4E26"
        }

        return null;
    }


    // 主方法：将原始文本转换为token
    public static String convertToTokenLabel(String inputText) {
        StringBuilder result = new StringBuilder();

        // 按行处理输入文本
        String[] lines = inputText.split("\n");
        for (String line : lines) {
            // 记录每行的缩进
            int indentLevel = 0;
            while (line.startsWith(" ")) {  // 计算前导空格的数量
                indentLevel++;
                line = line.substring(1);
            }

            // 生成缩进
            String indent = " ".repeat(indentLevel);

            // 去除前导空格后的line内容
            line = line.trim();

            // 处理mbr和entry节点
            if (line.startsWith("mbr")) {
                result.append(indent).append("<NL>");  // mbr节点用<NL>表示
            } else if (line.startsWith("entry")) {
                result.append(indent).append("<L>");  // entry节点用<L>表示
            }

            // 提取坐标部分并生成对应的token
            String token = generateTokenFromRect(line);
            if (token != null) {
                result.append(token);  // 添加生成的token
            }

            result.append("\n");
        }

        return result.toString().trim();
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
            String tokenSequence = convertToTokenLabel(treeString);

            // 输出 token 序列到新文件
            String outputFileName = outputFolder + File.separator + "tokens_" + file.getName();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName))) {
                writer.write(tokenSequence);
                System.out.println("Token sequence saved to: " + outputFileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 测试用例
    public static void main1(String[] args) {
        String inputText = "mbr=Rectangle [x1=0.0, y1=4.0, x2=4.0, y2=26.0]\n" +
                "  mbr=Rectangle [x1=0.0, y1=4.0, x2=4.0, y2=6.0]\n" +
                "    entry=Entry [value=27, geometry=Rectangle [x1=0.0, y1=4.0, x2=1.0, y2=5.0]]";

        String output = convertToTokenLabel(inputText);
        System.out.println(output);
    }

    public static void main(String[] args) {
        try {
            // 传入文件夹路径，处理该文件夹中的所有 .txt 文件
            String inputFolder = "preExperData/RTreeString";
            String outputFolder = "preExperData/RTreeToken";
//            String inputFolder = "D:\\project\\Java\\rtree_construct\\outputRTree\\";
//            String outputFolder = "D:\\project\\Java\\rtree_construct\\outputRTree1\\";

            processFilesInDirectory(inputFolder, outputFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
