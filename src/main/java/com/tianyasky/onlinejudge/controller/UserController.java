// controller/OjController.java
package com.tianyasky.onlinejudge.controller;

import com.tianyasky.onlinejudge.Judge.JudgePython;
import com.tianyasky.onlinejudge.model.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.tianyasky.onlinejudge.db.*;
import java.sql.SQLException;
import java.util.*;

@RestController
@RequestMapping("/api")
public class UserController {

    @PostMapping("/login")
    public boolean login(@RequestParam int user_id, @RequestParam String user_name, HttpSession session) throws SQLException, ClassNotFoundException {

        HashMap<Integer , User> users = select.queryUser("select * from USERS");
        if (users.containsKey(user_id) && users.get(user_id).UserName.equals(user_name)) {
            session.setAttribute("currentUserID", user_id); // OK!
            return true;
        }
        else {
            return false;
        }
    }

    @GetMapping("/profile")
public ResponseEntity<?> getProfile(HttpSession session) throws SQLException, ClassNotFoundException {
    Object attribute = session.getAttribute("currentUserID");

    if (attribute == null) {
        // 如果未登录，返回 401 未授权状态
        Map<String, String> error = new HashMap<>();
        error.put("status", "error");
        error.put("message", "用户未登录");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    Integer currentUserID = (Integer) attribute;
    User user = select.queryUserById(currentUserID);

    if (user == null) {
        // 用户不存在，返回 404
        Map<String, String> error = new HashMap<>();
        error.put("status", "error");
        error.put("message", "用户不存在");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // 成功获取用户信息，返回 200 OK
    return ResponseEntity.ok(user);
}

    @GetMapping("/queryUser/{userId}")
    public User queryUser(@PathVariable int userId) throws SQLException, ClassNotFoundException {
        return select.queryUserById(userId);
    };

    @GetMapping("/problems")
    public List<Problem> getProblems() throws SQLException, ClassNotFoundException {
        return select.queryAllProblem();
    }

    @GetMapping("/problems/{id}")
    public Problem getProblem(@PathVariable int id) throws SQLException, ClassNotFoundException {
        return select.queryProblemById(id);
    }

    @PostMapping("/get_user_information")
    public List<User> get_user_information() throws SQLException, ClassNotFoundException {
        LinkedHashMap<Integer, User> users = select.queryUser("SELECT * FROM USERS ORDER BY TO_NUMBER(USER_ID) ASC");
        return new ArrayList<>(users.values()); // 更安全的类型转换
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request,
                                                      HttpServletResponse response) {
        // 1. 使Session失效
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // 2. 清除Cookie（可选）
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("JSESSIONID")) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }

        // 3. 返回JSON响应
        Map<String, String> result = new HashMap<>();
        result.put("status", "success");
        result.put("message", "退出成功");
        return ResponseEntity.ok(result);
    }
    // 添加用户
    @PostMapping("/addUser")
    public boolean addUser(@RequestParam String user_name){
        User user = new User();
        user.admin = false;
        user.UserName = user_name;
        user.User_ID = select.queryMaxUserId()+1;
        insert.writeUser(user);
        return true;
    };
    @PostMapping("/deleteUser")
    public boolean deleteUser(@RequestParam int user_id){
        return delete.deleteUser(user_id);
    }
    @PostMapping("/upDateUser")
    public boolean upDateUser(@RequestParam int user_id,@RequestParam String new_name, @RequestParam boolean isAdmin){
        return update.upDateUser(user_id,isAdmin,new_name);
    }
    @GetMapping("/GetAllProblems")
    public List<Problem> GetAllProblems() throws SQLException, ClassNotFoundException {
        return select.queryAllProblem();
    };
    @PostMapping("/addProblem")
    public boolean addProblem(@RequestBody Map<String, Object> requestData) {
        // 安全获取并转换 id
        Object idObj = requestData.get("id");
        int id;

        if (idObj == null) {
            throw new IllegalArgumentException("题目ID不能为空");
        }

        if (idObj instanceof Integer) {
            id = (Integer) idObj;
        } else if (idObj instanceof String) {
            try {
                id = Integer.parseInt((String) idObj);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("题目ID必须为整数", e);
            }
        } else {
            throw new IllegalArgumentException("题目ID格式不正确");
        }

        String title = (String) requestData.get("title");
        String difficulty = (String) requestData.get("difficulty");
        String description = (String) requestData.get("description");

        // 新增字段
        String input = (String) requestData.get("TestData");
        String output = (String) requestData.get("OutputData");
        String inputFormat = (String) requestData.get("InputFormat");
        String outputFormat = (String) requestData.get("OutputFormat");
        String hint = (String) requestData.get("Hint");

        Map<String, Integer> DIFFICULTY_MAP = Map.of(
                "简单", 0,
                "中等", 1,
                "困难", 2
        );

        Integer diffValue = DIFFICULTY_MAP.get(difficulty);
        if (diffValue == null) {
            throw new IllegalArgumentException("难度等级不支持：" + difficulty);
        }
        int diff = diffValue;

        return insert.writeProblem(id, title, description, diff, input, output, inputFormat, outputFormat, hint);
    }



    @PostMapping("/deleteProblem")
    public boolean deleteProblem(@RequestParam int que_id){
      return delete.deleteQuestion(que_id);
    };
    @PostMapping("/addTestCase")
    public ResponseEntity<?> addTestCase(@RequestBody TestCaseDTO dto) {
        // 保存评测点逻辑
        insert.writeTestCase(dto.problemId, dto.testcaseNumber, dto.input, dto.output);
        return ResponseEntity.ok(dto); // 返回保存结果
    }

    @GetMapping("/getTestCases")
    public ResponseEntity<List<TestCaseDTO>> getTestCases(@RequestParam int problemId) throws SQLException, ClassNotFoundException {
        // 查询评测点逻辑
        List<TestCaseDTO> list = select.queryTestCaseByProblemId(problemId);
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/deleteTestCase")
    public ResponseEntity<?> deleteTestCase(@RequestBody Map<String, String> payload) {
        String id = payload.get("id");
        delete.deleteTestCase(id);
        // 删除逻辑
        return ResponseEntity.ok().build();
    }
    // 获取题解列表通过问题id
    @GetMapping("/solutions")
    public ResponseEntity<List<SolutionProblem>> getSolutions(@RequestParam int problemId) throws SQLException, ClassNotFoundException {
            List<SolutionProblem> list = select.queryAllSolutionById(problemId);
        return ResponseEntity.ok(list);
    }
    @PostMapping("/addSolution")
    public ResponseEntity<?> addSolution(@RequestBody SolutionProblem solution) throws SQLException, ClassNotFoundException {
        // 添加题解逻辑
        int solutionId = select.getMaxSolutionId();
        int nextId = solutionId+1;
        insert.writeSolution(solution.problemId, nextId, solution.userId, solution.content);
        return ResponseEntity.ok(solution); // 添加成功返回题解对象
    }
    // 通过题解编号获取详细题解内容
    @GetMapping("/getSolutionDetail")
    public ResponseEntity<SolutionProblem> getSolutionDetail(@RequestParam int solutionId) throws SQLException, ClassNotFoundException {
        SolutionProblem solution = select.querySolutionById(solutionId);
        return ResponseEntity.ok(solution);
    }
    // 获取所有评测点
    @GetMapping("/getAllTestCases")
    public List<TestCaseDTO> getAllTestCases() throws SQLException, ClassNotFoundException {
        return select.queryAllTestCase();
    }

    // 进行提交
    @PostMapping("/submit")
    public ResponseEntity<Map<String, Object>> handleCodeSubmission(
            @RequestParam int problemId,
            @RequestParam int user_id,
            @RequestParam String code) {

        // 创建响应对象
        Map<String, Object> response = new HashMap<>();

        try {
            // 校验输入是否为空
            if (
                code == null || code.isEmpty()) {
                response.put("status", "error");
                response.put("message", "缺少必要参数");
                return ResponseEntity.badRequest().body(response);
            }

            // 插入提交记录到数据库
            boolean isSubmitted = insert.writeSubmission(problemId, user_id);

            if (!isSubmitted) {
                response.put("status", "error");
                response.put("message", "提交失败，请重试");
                return ResponseEntity.status(500).body(response);
            }

            // 返回成功响应
            response.put("status", "success");
            response.put("message", "代码提交成功");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // 处理异常并返回错误信息
            response.put("status", "error");
            response.put("message", "服务器内部错误: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    // 对评测点进行评测
    @GetMapping("/judgeTestCase")
    public ResponseEntity<JudgePython.JudgeResult> judgeTestCase(
            @RequestParam int problemId,
            @RequestParam int user_id,
            @RequestParam int case_id,
            @RequestParam String code) throws SQLException, ClassNotFoundException {

        TestCaseDTO caseDTO = select.queryTestCaseByProblemCaseId(problemId,case_id);
        JudgePython.JudgeResult result = JudgePython.judgePythonProgram(code, caseDTO.input, caseDTO.output, 5000);
        return ResponseEntity.ok(result);
    }
    // 全部通过时修改ALL_THROUGH
    @PostMapping("/updateAllThrough")
    public boolean updateAllThrough(@RequestParam int user_id,  @RequestParam int Problem_id) throws SQLException, ClassNotFoundException {
        return update.updateAllThrough(user_id,Problem_id);
    }

}
