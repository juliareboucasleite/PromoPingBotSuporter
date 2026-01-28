package com.promoping.bot.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO para operações com a tabela news_config.
 */
public class NewsConfigDAO {
    
    private static final Logger logger = LoggerFactory.getLogger(NewsConfigDAO.class);
    
    public NewsConfig getActiveConfig() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Criar tabela se não existir
            createTableIfNotExists(conn);
            
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM news_config WHERE IsActive = TRUE LIMIT 1");
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new NewsConfig(
                    rs.getInt("Id"),
                    rs.getString("ChannelId"),
                    rs.getInt("CheckInterval"),
                    rs.getString("MonitoredCategories"),
                    rs.getInt("MinImpactScore"),
                    rs.getBoolean("IsActive")
                );
            }
            return null;
        }
    }
    
    public void createOrUpdate(String channelId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            createTableIfNotExists(conn);
            
            PreparedStatement checkStmt = conn.prepareStatement(
                "SELECT Id FROM news_config WHERE IsActive = TRUE LIMIT 1");
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                int id = rs.getInt("Id");
                PreparedStatement updateStmt = conn.prepareStatement(
                    "UPDATE news_config SET ChannelId = ?, IsActive = TRUE, UpdatedAt = NOW() WHERE Id = ?");
                updateStmt.setString(1, channelId);
                updateStmt.setInt(2, id);
                updateStmt.executeUpdate();
            } else {
                PreparedStatement insertStmt = conn.prepareStatement(
                    "INSERT INTO news_config (ChannelId, IsActive) VALUES (?, TRUE)");
                insertStmt.setString(1, channelId);
                insertStmt.executeUpdate();
            }
        }
    }
    
    public void deactivate() throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "UPDATE news_config SET IsActive = FALSE WHERE IsActive = TRUE");
            stmt.executeUpdate();
        }
    }
    
    private void createTableIfNotExists(Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
            "CREATE TABLE IF NOT EXISTS news_config (" +
            "Id INT AUTO_INCREMENT PRIMARY KEY," +
            "ChannelId VARCHAR(50) NOT NULL," +
            "CheckInterval INT DEFAULT 60 COMMENT 'Intervalo em minutos'," +
            "MonitoredCategories TEXT COMMENT 'Categorias separadas por vírgula'," +
            "MinImpactScore INT DEFAULT 7 COMMENT 'Score mínimo de impacto (1-10)'," +
            "IsActive BOOLEAN DEFAULT TRUE," +
            "LastCheck TIMESTAMP NULL," +
            "CreatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "UpdatedAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci");
        stmt.execute();
    }
    
    public static class NewsConfig {
        private final int id;
        private final String channelId;
        private final int checkInterval;
        private final String monitoredCategories;
        private final int minImpactScore;
        private final boolean isActive;
        
        public NewsConfig(int id, String channelId, int checkInterval, String monitoredCategories, 
                         int minImpactScore, boolean isActive) {
            this.id = id;
            this.channelId = channelId;
            this.checkInterval = checkInterval;
            this.monitoredCategories = monitoredCategories;
            this.minImpactScore = minImpactScore;
            this.isActive = isActive;
        }
        
        public int getId() { return id; }
        public String getChannelId() { return channelId; }
        public int getCheckInterval() { return checkInterval; }
        public String getMonitoredCategories() { return monitoredCategories; }
        public int getMinImpactScore() { return minImpactScore; }
        public boolean isActive() { return isActive; }
    }
}
