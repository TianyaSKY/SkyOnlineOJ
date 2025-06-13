package com.tianyasky.onlinejudge.model;

import java.util.Objects;

public class TestCaseDTO {

    public int problemId;       // 题目ID
    public Integer testcaseNumber; // 评测点编号
    public String input;           // 输入数据
    public String output;          // 输出数据

    // 默认构造函数（Spring JSON 反序列化需要）
    public TestCaseDTO() {}

    // 全参构造函数
    public TestCaseDTO(int problemId, Integer testcaseNumber, String input, String output) {
        this.problemId = problemId;
        this.testcaseNumber = testcaseNumber;
        this.input = input;
        this.output = output;
    }
}
