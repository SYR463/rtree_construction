package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class StringToSequence {

    public static void main(String[] args) {
        // 示例字符串，模拟 R-tree 的字符串输出
        String rTreeString1 = "mbr=Rectangle [x1=16.467, y1=35.381, x2=39.685, y2=59.154]\n" +
                "  entry=Entry [value=5, geometry=Point [x=39.685, y=59.154]]\n" +
                "  entry=Entry [value=2, geometry=Point [x=16.467, y=35.381]]";

        String rTreeString = "mbr=Rectangle [x1=3.848, y1=38.174, x2=52.884, y2=241.495]\n" +
                "  mbr=Rectangle [x1=15.172, y1=171.717, x2=49.903, y2=241.495]\n" +
                "    entry=Entry [value=14, geometry=Point [x=49.903, y=171.717]]\n" +
                "    entry=Entry [value=8, geometry=Point [x=37.949, y=184.351]]\n" +
                "    entry=Entry [value=13, geometry=Point [x=17.01, y=176.82]]\n" +
                "    entry=Entry [value=2, geometry=Point [x=15.172, y=227.422]]\n" +
                "    entry=Entry [value=5, geometry=Point [x=38.749, y=241.495]]\n" +
                "  mbr=Rectangle [x1=3.848, y1=38.174, x2=52.884, y2=130.344]\n" +
                "    entry=Entry [value=12, geometry=Point [x=3.848, y=84.197]]\n" +
                "    entry=Entry [value=10, geometry=Point [x=4.484, y=130.344]]\n" +
                "    entry=Entry [value=22, geometry=Point [x=15.064, y=113.687]]\n" +
                "    entry=Entry [value=9, geometry=Point [x=27.44, y=115.041]]\n" +
                "    entry=Entry [value=21, geometry=Point [x=37.967, y=89.036]]\n" +
                "    entry=Entry [value=18, geometry=Point [x=50.98, y=97.491]]\n" +
                "    entry=Entry [value=31, geometry=Point [x=52.884, y=38.174]]\n" +
                "    entry=Entry [value=26, geometry=Point [x=13.674, y=68.055]]";

        System.out.println(rTreeString);
        System.out.println("----------------------");

        // 转换字符串为序列
        List<String> sequence = convertStringToSequence(rTreeString);
        System.out.println(sequence);
    }

    // 将 R-tree 的字符串输出转换为序列
    public static List<String> convertStringToSequence(String rTreeString) {
        List<String> sequence = new ArrayList<>();
        sequence.add("⟨BEG⟩"); // 开始标签

        Scanner scanner = new Scanner(rTreeString);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();

            if (line.startsWith("mbr=")) {
                // 处理非叶子节点的 MBR 信息
                sequence.add("⟨NL⟩");
                sequence.add(line.substring(4)); // 去掉 "mbr=" 部分，保留 MBR 信息
            } else if (line.startsWith("entry=")) {
                // 处理叶子节点条目
                sequence.add("⟨L⟩");
                sequence.add(line.substring(6)); // 去掉 "entry=" 部分，添加条目信息
            }
        }

        // 假设最大容量为 8，检查是否需要添加占位符
        int maxCapacity = 8;
        int actualEntries = (int) sequence.stream().filter(s -> s.equals("⟨L⟩")).count();
        if (actualEntries < maxCapacity) {
            for (int i = actualEntries; i < maxCapacity; i++) {
                sequence.add("⟨S⟩");
            }
        }

        sequence.add("⟨END⟩"); // 结束标签
        return sequence;
    }
}
