package com.promoping.bot.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO for counting_config table operations.
 */
public class CountingDAO {
    
    private static final Logger logger = LoggerFactory.getLogger(CountingDAO.class);
    
    public CountingConfig getConfig(String guildId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT * FROM counting_config WHERE GuildId = ?");
            stmt.setString(1, guildId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new CountingConfig(
                    rs.getString("GuildId"),
                    rs.getString("ChannelId"),
                    rs.getInt("CurrentNumber"),
                    rs.getInt("HighScore"),
                    rs.getString("LastUserId")
                );
            }
            return null;
        }
    }
    
    public void createConfig(String guildId, String channelId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO counting_config (GuildId, ChannelId, CurrentNumber, HighScore) VALUES (?, ?, 0, 0)");
            stmt.setString(1, guildId);
            stmt.setString(2, channelId);
            stmt.executeUpdate();
        }
    }
    
    public void updateConfig(String guildId, String channelId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "UPDATE counting_config SET ChannelId = ?, CurrentNumber = 0, LastUserId = NULL WHERE GuildId = ?");
            stmt.setString(1, channelId);
            stmt.setString(2, guildId);
            stmt.executeUpdate();
        }
    }
    
    public void resetCounting(String guildId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "UPDATE counting_config SET CurrentNumber = 0, LastUserId = NULL WHERE GuildId = ?");
            stmt.setString(1, guildId);
            stmt.executeUpdate();
        }
    }
    
    public void deleteConfig(String guildId) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM counting_config WHERE GuildId = ?");
            stmt.setString(1, guildId);
            stmt.executeUpdate();
        }
    }
    
    public static class CountingConfig {
        private final String guildId;
        private final String channelId;
        private final int currentNumber;
        private final int highScore;
        private final String lastUserId;
        
        public CountingConfig(String guildId, String channelId, int currentNumber, int highScore, String lastUserId) {
            this.guildId = guildId;
            this.channelId = channelId;
            this.currentNumber = currentNumber;
            this.highScore = highScore;
            this.lastUserId = lastUserId;
        }
        
        public String getGuildId() { return guildId; }
        public String getChannelId() { return channelId; }
        public int getCurrentNumber() { return currentNumber; }
        public int getHighScore() { return highScore; }
        public String getLastUserId() { return lastUserId; }
    }
}
