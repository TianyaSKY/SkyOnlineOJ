package com.tianyasky.onlinejudge.Judge;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.*;

public class JudgeJava {

    /**
     * 评判Java程序的正确性
     * @param javaCode Java程序代码
     * @param input 输入数据
     * @param expectedOutput 预期输出
     * @param timeLimit 时间限制(毫秒)
     * @return 评判结果
     */
    public static JudgeResult judgeJavaProgram(
            String javaCode,
            String input,
            String expectedOutput,
            long timeLimit) {

        // 1. 创建临时Java源文件
        File sourceFile;
        try {
            sourceFile = createTempJavaFile(javaCode);
        } catch (IOException e) {
            return new JudgeResult(false, "无法创建临时Java文件: " + e.getMessage());
        }

        // 2. 编译Java文件
        File classFile = new File(sourceFile.getParent(), getClassName(sourceFile) + ".class");
        if (!compileJava(sourceFile)) {
            return new JudgeResult(false, "编译失败");
        }

        // 3. 运行Java程序并获取输出
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<ProcessOutput> future = executor.submit(() -> {
            ProcessBuilder pb = new ProcessBuilder("java", "-cp", sourceFile.getParent(), getClassName(sourceFile));
            pb.redirectErrorStream(true);

            Process process = null;
            try {
                process = pb.start();

                // 写入输入数据
                try (OutputStream os = process.getOutputStream();
                     PrintWriter writer = new PrintWriter(os)) {
                    writer.print(input);
                    writer.flush();
                }

                // 读取输出
                StringBuilder output = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n");
                    }
                }

                int exitCode = process.waitFor();
                return new ProcessOutput(output.toString().trim(), exitCode);

            } catch (Exception e) {
                return new ProcessOutput("执行错误: " + e.getMessage(), -1);
            } finally {
                if (process != null) {
                    process.destroy();
                }
            }
        });

        ProcessOutput processOutput;
        try {
            processOutput = future.get(timeLimit, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            return new JudgeResult(false, "程序运行超时");
        } catch (ExecutionException | InterruptedException e) {
            return new JudgeResult(false, "程序执行错误: " + e.getMessage());
        } finally {
            executor.shutdownNow();
            sourceFile.delete();
            classFile.delete();
        }

        // 4. 检查退出码
        if (processOutput.exitCode != 0) {
            return new JudgeResult(false, "程序异常退出，退出码: " + processOutput.exitCode);
        }

        // 5. 比较输出
        String actualOutput = processOutput.output;
        boolean isCorrect = normalizeOutput(actualOutput).equals(normalizeOutput(expectedOutput));

        return new JudgeResult(isCorrect, isCorrect ? "答案正确" : "答案错误");
    }

    // 创建临时Java源文件
    private static File createTempJavaFile(String javaCode) throws IOException {
        File tempDir = Files.createTempDirectory("oj_java_").toFile();
        File tempFile = new File(tempDir, "Solution.java"); // 假设类名为 Solution
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(javaCode);
        }
        return tempFile;
    }

    // 提取类名（从Java代码中解析）
    private static String getClassName(File javaFile) {
        String className = "Solution"; // 默认类名
        try (BufferedReader reader = new BufferedReader(new FileReader(javaFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().startsWith("public class")) {
                    int start = line.indexOf("class") + 6;
                    int end = line.indexOf("{", start);
                    className = line.substring(start, end > 0 ? end : line.length()).trim();
                    break;
                }
            }
        } catch (IOException ignored) {}
        return className;
    }

    // 编译Java文件
    private static boolean compileJava(File sourceFile) {
        try {
            ProcessBuilder pb = new ProcessBuilder("javac", sourceFile.getAbsolutePath());
            Process process = pb.start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            return false;
        }
    }

    // 标准化输出（去除多余空格和换行符）
    private static String normalizeOutput(String output) {
        return output.trim().replaceAll("\\s+", " ");
    }

    // 内部类：存储进程输出和退出码
    private static class ProcessOutput {
        String output;
        int exitCode;

        ProcessOutput(String output, int exitCode) {
            this.output = output;
            this.exitCode = exitCode;
        }
    }

    // 评判结果类
    public static class JudgeResult {
        private final boolean isCorrect;
        private final String message;

        JudgeResult(boolean isCorrect, String message) {
            this.isCorrect = isCorrect;
            this.message = message;
        }

        public boolean isCorrect() {
            return isCorrect;
        }

        public String getMessage() {
            return message;
        }
    }

    // 示例用法
    public static void main(String[] args) {
        String javaCode = "import java.util.Scanner;\n\npublic class Solution {\n    public static void main(String[] args) {\n        Scanner sc = new Scanner(System.in);\n        int n = sc.nextInt();\n        int m = sc.nextInt();\n        System.out.println(n + m);\n    }\n}";

        // 测试用例1
        String input1 = "3 5";
        String expectedOutput1 = "8";
        JudgeResult result1 = judgeJavaProgram(javaCode, input1, expectedOutput1, 2000);
        System.out.println("测试用例1: " + result1.getMessage());

        // 测试用例2
        String input2 = "10 20";
        String expectedOutput2 = "30";
        JudgeResult result2 = judgeJavaProgram(javaCode, input2, expectedOutput2, 2000);
        System.out.println("测试用例2: " + result2.getMessage());
    }
}
