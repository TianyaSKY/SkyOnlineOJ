package com.tianyasky.onlinejudge.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;

public class update {
    private static final String URL = "jdbc:oracle:thin:@localhost:1521:FREE";
    private static final String USER = "c##OnlineJudge";
    private static final String PASSWORD = "a1960446107";

    // 封装数据库连接
    private static Connection getConnection() throws SQLException, ClassNotFoundException {
        Class.forName("oracle.jdbc.driver.OracleDriver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // 执行更新用户语句
    public static boolean upDateUser(int User_ID,boolean newIsAdmin, String newName) {
        String sql = "UPDATE USERS SET USER_NAME = ?, IS_ADMIN = ? WHERE USER_ID = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 设置参数
            pstmt.setString(1, newName);
            pstmt.setInt(3, User_ID);
            pstmt.setBoolean(2, newIsAdmin);

            // 执行插入
            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    // 更新该用户最后一个提交点为 True
    public static boolean updateAllThrough(int User_id, int Problem_id) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE SUBMIT SET ALL_THROUGH = 1 WHERE SUBMIT_ID IN (SELECT MAX(SUBMIT_ID) FROM SUBMIT WHERE USER_ID = ? AND QUE_ID = ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // 设置参数
            pstmt.setInt(1, User_id);
            pstmt.setInt(2, Problem_id);

            // 执行更新
            int affectedRows = pstmt.executeUpdate();

            return affectedRows > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}