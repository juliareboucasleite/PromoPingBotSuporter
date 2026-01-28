package com.promoping.bot.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BugReportDAO {

    public int saveBug(String titulo, String descricao, String tipo, String prioridade, String status)
            throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO bugsprojetos (Titulo, Descricao, Tipo, Prioridade, Status, DataCriacao) " +
                            "VALUES (?, ?, ?, ?, ?, NOW())",
                    PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, titulo);
            stmt.setString(2, descricao);
            stmt.setString(3, tipo);
            stmt.setString(4, prioridade);
            stmt.setString(5, status);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }
}
