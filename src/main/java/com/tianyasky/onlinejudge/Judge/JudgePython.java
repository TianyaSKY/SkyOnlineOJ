package com.tianyasky.onlinejudge.Judge;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class JudgePython {

    /**
     * 评判Python程序的正确性
     * @param pythonCode Python程序代码
     * @param input 输入数据
     * @param expectedOutput 预期输出
     * @param timeLimit 时间限制(毫秒)
     * @return 评判结果
     */
    public static JudgeResult judgePythonProgram(
            String pythonCode,
            String input,
            String expectedOutput,
            long timeLimit) {

        // 1. 将Python代码保存到临时文件
        File pythonFile;
        try {
            pythonFile = createTempPythonFile(pythonCode);
        } catch (IOException e) {
            return new JudgeResult(false, "无法创建临时Python文件: " + e.getMessage());
        }

        // 2. 准备执行Python进程
        ProcessBuilder pb = new ProcessBuilder("python", pythonFile.getAbsolutePath());
        pb.redirectErrorStream(true);

        // 3. 执行并获取输出
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<ProcessOutput> future = executor.submit(() -> {
            Process process = pb.start();

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
            pythonFile.delete();
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

    // 创建临时Python文件
    private static File createTempPythonFile(String pythonCode) throws IOException {
        File tempFile = File.createTempFile("oj_python_", ".py");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(pythonCode);
        }
        return tempFile;
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
        boolean isCorrect;
        String message;

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
        String pythonCode = "import math\n\nn, m = map(int, input().split())\nsticks = list(map(int,input().split()))\nright = int(1e10)\nleft = 0\ns = sum(sticks)\ndef f(length):  # 把木棒切到<=length长度需要多少次\n    cnt = 0\n    for stick in sticks:\n        if stick > length:\n            cnt += stick // length\n    return cnt <= m\nwhile left <= right:\n    mid = (left + right) >> 1\n    if f(mid):\n        right = mid - 1\n    else:\n        left = mid + 1\nprint(right + 1)";

        // 测试用例1
        String input1 = "3 8\n10 24 15";
        String expectedOutput1 = "6";
        JudgeResult result1 = judgePythonProgram(pythonCode, input1, expectedOutput1, 2000);
        System.out.println("测试用例1: " + result1.getMessage());

        // 测试用例2
        String input2 = "4 20\n4 8 12 16";
        String expectedOutput2 = "2";
        JudgeResult result2 = judgePythonProgram(pythonCode, input2, expectedOutput2, 2000);
        System.out.println("测试用例2: " + result2.getMessage());
    }
}