package org.ll.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Sql {
    private Connection connection;
    private String sql = "";
    private List<Object> params = new ArrayList<>();

    public Sql(Connection connection){
        this.connection = connection;
    }



    public Sql append(String sql, Object... params) {
        this.sql += " "+ sql;
        for(Object obj: params){
            this.params.add(obj);
        }
        return this;
    }

    public long insert() {
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            stmt.executeUpdate();

            // Get generated keys
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("SQL 실행 중 오류 발생: " + e.getMessage());
        }
        return -1;
    }

    public int update () {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            stmt.executeUpdate();
            return stmt.getUpdateCount();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("SQL 실행 중 오류 발생: " + e.getMessage());
        }
    }
}
