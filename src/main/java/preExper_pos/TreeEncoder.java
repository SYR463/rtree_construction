/**
 *
 *
 *
 * 标记结果：
 * 4 <NL> A0 E5
 * 41 <NL> A0 C5
 * 411 <L> B0 C1
 * 412 <L> B3 C4
 * 413 <L> A4 B5
 * 42 <NL> D2 E5
 * 421 <L> D2 E3
 * 422 <L> D2 E3
 * 423 <L> D4 E5
 */


package preExper_pos;

import java.util.*;

public class TreeEncoder {
    // 用于存储节点的父子关系
    static class Node {
        String label;
        List<Node> children = new ArrayList<>();

        public Node(String label) {
            this.label = label;
        }
    }

    public static void main(String[] args) {
        // 输入数据
        String[] input = {
                "<NL> A0 E5",
                "  <NL> A0 C5",
                "    <L> B0 C1",
                "    <L> B3 C4",
                "    <L> A4 B5",
                "  <NL> D2 E5",
                "    <L> D2 E3",
                "    <L> D2 E3",
                "    <L> D4 E5"
        };

        // 解析树结构
        Node root = buildTree(input);

        // 遍历并编码树
        Map<Node, String> nodeCodeMap = new LinkedHashMap<>();
        encodeTree(root, nodeCodeMap, new int[]{1}, new StringBuilder(), 1);

        // 修改根节点的编码为 4
        replaceRootCode(nodeCodeMap, "4");

        // 输出编码结果
        for (Map.Entry<Node, String> entry : nodeCodeMap.entrySet()) {
            System.out.println(entry.getValue() + " " + entry.getKey().label);
        }
    }

    // 递归构建树结构
    private static Node buildTree(String[] input) {
        Stack<Node> stack = new Stack<>();
        Node root = null;

        for (String line : input) {
            int indentLevel = countIndentation(line); // 计算当前行的缩进层级
            String label = line.trim(); // 获取当前行的标签

            // 创建新节点
            Node newNode = new Node(label);

            // 处理根节点的情况
            if (stack.isEmpty() && indentLevel == 0) {
                root = newNode;
            } else {
                // 找到当前节点的父节点
                while (stack.size() > indentLevel) {
                    stack.pop();
                }

                // 如果栈非空，设置父节点
                if (!stack.isEmpty()) {
                    stack.peek().children.add(newNode);
                }
            }

            // 将当前节点压入栈中
            stack.push(newNode);
        }
        return root;
    }

    // 计算缩进的层级
    private static int countIndentation(String line) {
        int indentLevel = 0;
        while (line.startsWith("  ")) {
            indentLevel++;
            line = line.substring(2); // 去除前两个空格
        }
        return indentLevel;
    }

    // 编码树结构
    private static void encodeTree(Node node, Map<Node, String> nodeCodeMap, int[] counter, StringBuilder currentPath, int level) {
        // 为当前节点设置编码，编码按层级递增
        String currentCode = currentPath.append(counter[0]++).toString();
        nodeCodeMap.put(node, currentCode);

        // 为每个子节点递归编码，每一层的计数器从1开始
        int childCounter = 1;
        for (Node child : node.children) {
            encodeTree(child, nodeCodeMap, new int[]{childCounter}, new StringBuilder(currentCode), level + 1);
            childCounter++;
        }
    }

    // 替换所有节点编码的首元素为目标值
    private static void replaceRootCode(Map<Node, String> nodeCodeMap, String newRootCode) {
        for (Map.Entry<Node, String> entry : nodeCodeMap.entrySet()) {
            String code = entry.getValue();
            // 直接替换首元素为目标值（此处为 "4"）
            String updatedCode = newRootCode + code.substring(1);
            entry.setValue(updatedCode);
        }
    }
}
