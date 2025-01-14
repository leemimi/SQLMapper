package org.ll.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SimpleDb {
    private String baseUrl;  // 데이터베이스 이름 없이 연결
    private String fullUrl;  // 데이터베이스 이름 포함
    private String username;
    private String password;
    private String database;

    public SimpleDb(String url, String username, String password, String database) {
        this.baseUrl = "jdbc:mysql://" + url + "?serverTimezone=UTC&useSSL=false";
        this.fullUrl = "jdbc:mysql://" + url + "/" + database + "?serverTimezone=UTC&useSSL=false";
        this.username = username;
        this.password = password;
        this.database = database;

        try {
            ensureDatabaseExists(); // 데이터베이스 존재 여부 확인 및 생성
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("데이터베이스 생성 실패: " + e.getMessage());
        }
    }

    // 데이터베이스가 없으면 생성
    private void ensureDatabaseExists() throws SQLException {
        // 데이터베이스 이름 없이 연결
        try (Connection conn = DriverManager.getConnection(baseUrl, username, password)) {
            String sql = "CREATE DATABASE IF NOT EXISTS " + database;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.execute();
                System.out.println("데이터베이스 확인 또는 생성 완료: " + database);
            }
        }
    }

    public Connection getConnection() throws SQLException {
        // 데이터베이스 이름 포함 URL로 연결
        return DriverManager.getConnection(fullUrl, username, password);
    }

    public void run(String sql, Object... params) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void setDevMode(boolean b) {
        try {
            getConnection();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("데이터베이스 연결 실패: " + e.getMessage());
        }
    }

    public Sql genSql () {
        try {
            return new Sql(getConnection());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
