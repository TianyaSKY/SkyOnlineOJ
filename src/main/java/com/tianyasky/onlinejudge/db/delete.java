package com.tianyasky.onlinejudge.db;

import com.tianyasky.onlinejudge.model.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class delete {
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:FREE";
    private static final String USER = "c##OnlineJudge";
    private static final String PASSWORD = "a1960446107";

    // 封装数据库连接
    private static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // 执行删除用户
    public static boolean deleteUser(int user_id) {
        String sql = "delete from USERS where USER_ID = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 设置参数
            pstmt.setInt(1, user_id);

            // 执行插入
            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    // 执行删除用户
    public static boolean deleteQuestion(int question_id) {
        String sql = "delete from QUESTION where QUE_ID = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 设置参数
            pstmt.setInt(1, question_id);

            // 执行插入
            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    // 按测试点id删除测试点
    public static boolean deleteTestCase(String testcaseNumber) {
        String sql = "delete from EVALUATION_POINT where EVALUATE_POINT_ID = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 设置参数
            pstmt.setString(1, testcaseNumber);

            // 执行插入
            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}