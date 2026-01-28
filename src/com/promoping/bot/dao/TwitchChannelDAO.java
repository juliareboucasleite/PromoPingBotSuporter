package com.promoping.bot.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operações com a tabela twitch_channels.
 */
public class TwitchChannelDAO {
    
    private static final Logger logger = LoggerFactory.getLogger(TwitchChannelDAO.class);
    
    public List<TwitchChannel> getAllChannels() throws SQLException {
        List<TwitchChannel> channels = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT ChannelName, IsLive, LastLiveCheck FROM twitch_channels ORDER BY ChannelName");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                channels.add(new TwitchChannel(
                    rs.getString("ChannelName"),
                    rs.getBoolean("IsLive"),
                    rs.getTimestamp("LastLiveCheck")
                ));
            }
        }
        return channels;
    }
    
    public boolean channelExists(String channelName) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT Id FROM twitch_channels WHERE ChannelName = ? OR ChannelName LIKE ?");
            stmt.setString(1, channelName.toLowerCase());
            stmt.setString(2, "%" + channelName.toLowerCase() + "%");
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }
    
    public void addChannel(String channelName) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "INSERT INTO twitch_channels (ChannelName) VALUES (?)");
            stmt.setString(1, channelName.toLowerCase());
            stmt.executeUpdate();
        }
    }
    
    public void removeChannel(String channelName) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "DELETE FROM twitch_channels WHERE ChannelName = ? OR ChannelName LIKE ?");
            stmt.setString(1, channelName.toLowerCase());
            stmt.setString(2, "%" + channelName.toLowerCase() + "%");
            stmt.executeUpdate();
        }
    }
    
    public static class TwitchChannel {
        private final String channelName;
        private final boolean isLive;
        private final java.sql.Timestamp lastLiveCheck;
        
        public TwitchChannel(String channelName, boolean isLive, java.sql.Timestamp lastLiveCheck) {
            this.channelName = channelName;
            this.isLive = isLive;
            this.lastLiveCheck = lastLiveCheck;
        }
        
        public String getChannelName() { return channelName; }
        public boolean isLive() { return isLive; }
        public java.sql.Timestamp getLastLiveCheck() { return lastLiveCheck; }
    }
}
