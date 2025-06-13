package com.tianyasky.onlinejudge.model;

public class SolutionProblem {
    public int problemId;
    public int solutionId;
    public int userId;
    public String content;
    public String authorName;
    public SolutionProblem()
    {
        this.problemId = 0;
        this.solutionId = 0;
        this.userId = 0;
        this.content = "";
        this.authorName = "";
    }
}
