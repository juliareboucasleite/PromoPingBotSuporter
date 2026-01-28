package com.promoping.bot.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO para operações com a tabela reviews.
 */
public class ReviewDAO {
    
    private static final Logger logger = LoggerFactory.getLogger(ReviewDAO.class);
    
    /**
     * Obtém o ReferenciaID do usuário pelo discord_id.
     */
    public String getReferenciaIdByDiscordId(String discordId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT ReferenciaID FROM utilizadores WHERE discord_id = ?");
            stmt.setString(1, discordId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("ReferenciaID");
            }
            return null;
        }
    }
    
    /**
     * Verifica se já existe uma review recente (últimos 5 minutos) do mesmo usuário e tipo.
     */
    public boolean hasRecentReview(String referenciaId, String tipo) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT Id FROM reviews WHERE ReferenciaID = ? AND Tipo = ? AND CreatedAt > DATE_SUB(NOW(), INTERVAL 5 MINUTE)");
            stmt.setString(1, referenciaId);
            stmt.setString(2, tipo);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }
    
    /**
     * Salva uma nova review.
     */
    public int saveReview(String referenciaId, String tipo, String texto, Integer rating, boolean isAnonimo) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO reviews (ReferenciaID, Tipo, Texto, Rating, IsAnonimo) VALUES (?, ?, ?, ?, ?)",
                PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, referenciaId);
            stmt.setString(2, tipo);
            stmt.setString(3, texto != null ? texto : "");
            stmt.setObject(4, rating);
            stmt.setInt(5, isAnonimo ? 1 : 0);
            
            stmt.executeUpdate();
            
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }
}
