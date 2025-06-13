package com.tianyasky.onlinejudge.db;

import com.tianyasky.onlinejudge.model.Problem;
import com.tianyasky.onlinejudge.model.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.util.Map;

public class insert {
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:FREE";
    private static final String USER = "c##OnlineJudge";
    private static final String PASSWORD = "a1960446107";

    // 封装数据库连接
    private static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // 执行插入用户语句
    public static boolean writeUser(User user) {
        String sql = "INSERT INTO USERS (USER_ID, USER_NAME, IS_ADMIN) " +
                "VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 设置参数
            pstmt.setInt(1, user.User_ID);
            pstmt.setString(2, user.UserName);
            if (user.admin){
                pstmt.setBoolean(3, true);
            }
            else {
                pstmt.setBoolean(3, false);
            }

            // 执行插入
            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean writeProblem(int id, String title, String description, int diff,
                                       String testData, String outputData,
                                       String inputFormat, String outputFormat, String hint) {
        String sql = "INSERT INTO QUESTION (QUE_ID, QUE_TITLE, QUE_CONTENT, QUE_DIFF, QUE_TESTI, QUE_TESTO, INPUTFORMAT, OUTPUTFORMAT, HINT) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.setString(2, title);
            pstmt.setString(3, description);
            pstmt.setInt(4, diff);
            pstmt.setString(5, testData);
            pstmt.setString(6, outputData);
            pstmt.setString(7, inputFormat);
            pstmt.setString(8, outputFormat);
            pstmt.setString(9, hint);

            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean writeTestCase(int Problem_id, int Testcase_number, String input, String output) {
        String sql = "INSERT INTO EVALUATION_POINT (EVALUATE_POINT_ID, QUE_ID, INPUT_DATA, OUTPUT_DATA) " + "VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 设置参数
            pstmt.setString(1, String.valueOf(Testcase_number));
            pstmt.setInt(2, Problem_id);
            pstmt.setString(3, input);
            pstmt.setString(4, output);

            // 执行插入
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
            }
    }
    public static boolean writeSolution(int Problem_id, int Solution_id, int User_id, String content) {
        String sql = "INSERT INTO QUESTION_ANSWER (QUE_ID, QUESTION_ANS_ID, USER_ID, QUESTION_ANS_CONTENT) " + "VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, Problem_id);
            pstmt.setInt(2, Solution_id);
            pstmt.setInt(3, User_id);
            pstmt.setString(4, content);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public static boolean writeSubmission(int Problem_id, int User_id){
        String nextId = String.valueOf(Integer.parseInt(select.queryMaxSubmitId())+1);
        String sql = "INSERT INTO SUBMIT (SUBMIT_ID, QUE_ID, USER_ID,ALL_THROUGH) " + "VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nextId);
            pstmt.setInt(2, Problem_id);
            pstmt.setInt(3, User_id);
            pstmt.setInt(4,0);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    };
};

