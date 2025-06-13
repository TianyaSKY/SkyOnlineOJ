// model/Problem.java
package com.tianyasky.onlinejudge.model;

import java.util.ArrayList;
import java.util.List;

public class Problem {
    public int id;
    public String title;
    public String description;
    public String TestData;
    public String OutputData;
    public String Diff;
    public String Hint;
    public String InputFormat;
    public String OutputFormat;
    public List<Integer> TestPoint;

    public Problem(int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.TestData = "";
        this.OutputData = "";
        this.TestPoint = new ArrayList<Integer>();
    }

    public Problem() {

    }
}
