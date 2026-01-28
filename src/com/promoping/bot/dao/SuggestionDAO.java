package com.promoping.bot.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SuggestionDAO {

    public int saveSuggestion(String titulo, String descricao, String plataforma, String prioridade, String status, int votos)
            throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO sugestoes (Titulo, Descricao, Plataforma, Prioridade, Status, Votos, DataCriacao) " +
                            "VALUES (?, ?, ?, ?, ?, ?, NOW())",
                    PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, titulo);
            stmt.setString(2, descricao);
            stmt.setString(3, plataforma);
            stmt.setString(4, prioridade);
            stmt.setString(5, status);
            stmt.setInt(6, votos);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }
}
