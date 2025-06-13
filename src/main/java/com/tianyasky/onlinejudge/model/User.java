// model/User.java
package com.tianyasky.onlinejudge.model;

import java.util.List;

public class User {
    public String UserName;
    public int User_ID;
    public boolean admin;
    public List<String> Success;  // 成功通过
    public List<String> All_submit;  // 总提交
    public int SuccessCount;
    public int SubmitCount;
}
