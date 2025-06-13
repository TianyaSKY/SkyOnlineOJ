package com.tianyasky.onlinejudge.db;

import com.tianyasky.onlinejudge.model.*;

import java.sql.*;
import java.util.*;

public class select {
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:FREE";
    private static final String USER = "c##OnlineJudge";
    private static final String PASSWORD = "a1960446107";

    // 封装数据库连接
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // 查询用户表
    public static LinkedHashMap<Integer , User> queryUser(String sql) throws SQLException, ClassNotFoundException {
        LinkedHashMap<Integer , User> Users = new LinkedHashMap<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                User user = new User();
                user.UserName = rs.getString("USER_NAME").trim();
                user.User_ID = rs.getInt("USER_ID");
                user.admin = rs.getBoolean("IS_ADMIN");
                Users.put(user.User_ID, user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Users;
    }

    // 获取最大用户id
    public static int queryMaxUserId(){
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("select MAX(USER_ID) from USERS")) {
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    };
    // 按id查询用户
    public static User queryUserById(int userId) throws SQLException, ClassNotFoundException {
        String sql = "SELECT " +
                     "USERS.USER_ID, " +
                     "USERS.USER_NAME, " +
                     "USERS.IS_ADMIN, " +
                     "COUNT(SUBMIT.SUBMIT_ID) AS SUBMIT_COUNT, " +
                     "SUM(CASE WHEN SUBMIT.ALL_THROUGH = 1 THEN 1 ELSE 0 END) AS SUCCESS_COUNT " +
                     "FROM USERS " +
                     "LEFT JOIN SUBMIT ON USERS.USER_ID = SUBMIT.USER_ID " +
                     "WHERE USERS.USER_ID = ? " +
                     "GROUP BY USERS.USER_ID, USERS.USER_NAME, USERS.IS_ADMIN";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.User_ID = rs.getInt("USER_ID");
                user.UserName = rs.getString("USER_NAME").trim();
                user.admin = rs.getBoolean("IS_ADMIN");
                user.SubmitCount = rs.getInt("SUBMIT_COUNT"); // 总提交数
                user.SuccessCount = rs.getInt("SUCCESS_COUNT"); // 成功通过数
                return user;
            }
        } catch (SQLException e) {
            System.err.println("Error querying user by ID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    // 获取所有题目
    public static List<Problem> queryAllProblem() throws SQLException, ClassNotFoundException {
        List<Problem> problems = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("select * from QUESTION ORDER BY QUE_ID")) {
            while (rs.next()) {
                Problem problem = new Problem();
                problem.id = rs.getInt("QUE_ID");
                problem.description = rs.getString("QUE_CONTENT");
                problem.title = rs.getString("QUE_TITLE");
                problem.Diff = rs.getString("QUE_DIFF");
                problem.TestData = rs.getString("QUE_TESTI");
                problem.OutputData = rs.getString("QUE_TESTO");
                problem.Hint = rs.getString("HINT");
                problem.InputFormat = rs.getString("INPUTFORMAT");
                problem.OutputFormat = rs.getString("OUTPUTFORMAT");

                problems.add(problem);
            }
            return problems;


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    };
    // 获取所有测试点
    public static List<TestCaseDTO> queryAllTestCase() throws SQLException, ClassNotFoundException {
        List<TestCaseDTO> testCases = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("select * from EVALUATION_POINT ORDER BY EVALUATE_POINT_ID")) {
            while (rs.next()){
                TestCaseDTO testCase = new TestCaseDTO();
                testCase.problemId = rs.getInt("QUE_ID");
                testCase.testcaseNumber = rs.getInt("EVALUATE_POINT_ID");
                testCase.input = rs.getString("INPUT_DATA");
                testCase.output = rs.getString("OUTPUT_DATA");
                testCases.add(testCase);
            }
            return testCases;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    };
    // 按id查询测试点
    public static List<TestCaseDTO> queryTestCaseByProblemId(int problemId) throws SQLException, ClassNotFoundException {
        List<TestCaseDTO> testCases = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("select * from EVALUATION_POINT where QUE_ID = " + problemId)) {
             while (rs.next()){
                TestCaseDTO testCase = new TestCaseDTO();
                testCase.problemId = rs.getInt("QUE_ID");
                testCase.testcaseNumber = rs.getInt("EVALUATE_POINT_ID");
                testCase.input = rs.getString("INPUT_DATA");
                testCase.output = rs.getString("OUTPUT_DATA");
                testCases.add(testCase);
                }
             return testCases;
             }

    }

    public static List<SolutionProblem> queryAllSolutionById(int SID) throws SQLException, ClassNotFoundException {
    List<SolutionProblem> solutions = new ArrayList<>();
    String sql = "SELECT * FROM QUESTION_ANSWER WHERE QUE_ID = ? ORDER BY QUE_ID";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, SID); // 防止 SQL 注入
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                SolutionProblem solution = new SolutionProblem();
                solution.userId = rs.getInt("USER_ID");
                solution.problemId = rs.getInt("QUE_ID");
                solution.solutionId = rs.getInt("QUESTION_ANS_ID");
                solution.content = rs.getString("QUESTION_ANS_CONTENT");
                User u = queryUserById(solution.userId);
                solution.authorName = u.UserName;
                solutions.add(solution);
            }
        }
        return solutions;
    }
    public static int getMaxSolutionId() throws SQLException, ClassNotFoundException {
        String sql = "SELECT MAX(QUESTION_ANS_ID) AS MAX_ID FROM QUESTION_ANSWER";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("MAX_ID");
            }
        }
        return 0;
    }
    public static SolutionProblem querySolutionById(int SID) throws SQLException, ClassNotFoundException {
        SolutionProblem solution = new SolutionProblem();
        String sql = "SELECT * FROM QUESTION_ANSWER WHERE QUESTION_ANS_ID = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, SID); // 防止 SQL 注入
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                solution.userId = rs.getInt("USER_ID");
                solution.problemId = rs.getInt("QUE_ID");
                solution.solutionId = rs.getInt("QUESTION_ANS_ID");
                solution.content = rs.getString("QUESTION_ANS_CONTENT");
                solution.authorName = select.queryUserById(solution.userId).UserName;
            }
            return solution;
        }
    }
    public static String queryMaxSubmitId(){
        String sql = "SELECT MAX(SUBMIT_ID) AS MAX_ID FROM SUBMIT";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                if (rs.getString("MAX_ID") == null)
                    return "0";
                return rs.getString("MAX_ID");
            }
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return "0";
    };
    public static TestCaseDTO queryTestCaseByProblemCaseId(int problemId, int testcaseNumber) throws SQLException, ClassNotFoundException {
        TestCaseDTO testCase = new TestCaseDTO();
        String sql = "SELECT * FROM EVALUATION_POINT WHERE QUE_ID = ? AND EVALUATE_POINT_ID = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, problemId); // 防止 SQL 注入
            pstmt.setInt(2, testcaseNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                testCase.problemId = rs.getInt("QUE_ID");
                testCase.testcaseNumber = rs.getInt("EVALUATE_POINT_ID");
                testCase.input = rs.getString("INPUT_DATA");
                testCase.output = rs.getString("OUTPUT_DATA");
            }
        }
        return testCase;
    };
    public static Problem queryProblemById(int problemId) throws SQLException, ClassNotFoundException {
        Problem problem = new Problem();
        String sql = "SELECT * FROM QUESTION WHERE QUE_ID = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, problemId); // 防止 SQL 注入
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                problem.id = rs.getInt("QUE_ID");
                problem.description = rs.getString("QUE_CONTENT");
                problem.title = rs.getString("QUE_TITLE");
                problem.Diff = rs.getString("QUE_DIFF");
                problem.TestData = rs.getString("QUE_TESTI");
                problem.OutputData = rs.getString("QUE_TESTO");
                problem.Hint = rs.getString("HINT");
                problem.InputFormat = rs.getString("INPUTFORMAT");
                problem.OutputFormat = rs.getString("OUTPUTFORMAT");
            }
        }
        return problem;
    };
}

