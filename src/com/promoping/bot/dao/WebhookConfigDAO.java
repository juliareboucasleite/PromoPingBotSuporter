package com.promoping.bot.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO para operações com a tabela webhook_configs.
 */
public class WebhookConfigDAO {
    
    private static final Logger logger = LoggerFactory.getLogger(WebhookConfigDAO.class);
    
    public WebhookConfig getConfig(String type) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM webhook_configs WHERE Type = ? AND IsActive = TRUE LIMIT 1");
            stmt.setString(1, type);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new WebhookConfig(
                    rs.getInt("Id"),
                    rs.getString("Type"),
                    rs.getString("WebhookUrl"),
                    rs.getBoolean("IsActive")
                );
            }
            return null;
        }
    }
    
    public void createOrUpdate(String type, String webhookUrl) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Verificar se já existe
            PreparedStatement checkStmt = conn.prepareStatement(
                "SELECT Id FROM webhook_configs WHERE Type = ?");
            checkStmt.setString(1, type);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                // Atualizar
                PreparedStatement updateStmt = conn.prepareStatement(
                    "UPDATE webhook_configs SET WebhookUrl = ?, IsActive = TRUE WHERE Type = ?");
                updateStmt.setString(1, webhookUrl);
                updateStmt.setString(2, type);
                updateStmt.executeUpdate();
            } else {
                // Criar
                PreparedStatement insertStmt = conn.prepareStatement(
                    "INSERT INTO webhook_configs (Type, WebhookUrl, IsActive) VALUES (?, ?, TRUE)");
                insertStmt.setString(1, type);
                insertStmt.setString(2, webhookUrl);
                insertStmt.executeUpdate();
            }
        }
    }
    
    public static class WebhookConfig {
        private final int id;
        private final String type;
        private final String webhookUrl;
        private final boolean isActive;
        
        public WebhookConfig(int id, String type, String webhookUrl, boolean isActive) {
            this.id = id;
            this.type = type;
            this.webhookUrl = webhookUrl;
            this.isActive = isActive;
        }
        
        public int getId() { return id; }
        public String getType() { return type; }
        public String getWebhookUrl() { return webhookUrl; }
        public boolean isActive() { return isActive; }
    }
}
