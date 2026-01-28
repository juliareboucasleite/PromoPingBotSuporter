package com.promoping.bot.services;

import net.dv8tion.jda.api.JDA;
import com.promoping.bot.dao.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class StatusService {
    
    private static final Instant START_TIME = Instant.now();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", Locale.forLanguageTag("pt-PT"));
    
    public StatusInfo getStatusInfo(JDA jda) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Monitored products
            int productCount = 0;
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT COUNT(*) as total FROM produtos WHERE DeletedAt IS NULL")) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    productCount = rs.getInt("total");
                }
            }
            
            // Discord users
            int discordUsers = 0;
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT COUNT(*) as total FROM utilizadores WHERE discord_id IS NOT NULL AND discord_id != ''")) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    discordUsers = rs.getInt("total");
                }
            }
            
            // Changes today
            int changesToday = 0;
            try (PreparedStatement stmt = conn.prepareStatement(
                    "SELECT COUNT(*) as total FROM historicoprecos WHERE DATE(DataRegisto) = CURDATE()")) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    changesToday = rs.getInt("total");
                }
            }
            
            String prefix = System.getenv("DISCORD_PREFIX");
            if (prefix == null || prefix.isEmpty()) {
                prefix = "!";
            }
            
            String checkInterval = System.getenv("BOT_CHECK_INTERVAL");
            if (checkInterval == null || checkInterval.isEmpty()) {
                checkInterval = "5";
            }
            
            Duration uptime = Duration.between(START_TIME, Instant.now());
            long hours = uptime.toHours();
            long minutes = uptime.toMinutes() % 60;
            String uptimeStr = hours + "h " + minutes + "m";
            
            return new StatusInfo(
                    false, // isMonitoring - needs to be managed by bot
                    uptimeStr,
                    Instant.now().atZone(java.time.ZoneId.systemDefault()).format(FORMATTER),
                    productCount,
                    discordUsers,
                    changesToday,
                    checkInterval,
                    jda.getGuilds().size(),
                    prefix
            );
            
        } catch (Exception e) {
            throw new RuntimeException("Error getting status", e);
        }
    }
    
    public static class StatusInfo {
        private final boolean isMonitoring;
        private final String uptime;
        private final String lastCheck;
        private final int productCount;
        private final int discordUsers;
        private final int changesToday;
        private final String checkInterval;
        private final int guildCount;
        private final String prefix;
        
        public StatusInfo(boolean isMonitoring, String uptime, String lastCheck, 
                         int productCount, int discordUsers, int changesToday,
                         String checkInterval, int guildCount, String prefix) {
            this.isMonitoring = isMonitoring;
            this.uptime = uptime;
            this.lastCheck = lastCheck;
            this.productCount = productCount;
            this.discordUsers = discordUsers;
            this.changesToday = changesToday;
            this.checkInterval = checkInterval;
            this.guildCount = guildCount;
            this.prefix = prefix;
        }
        
        public boolean isMonitoring() { return isMonitoring; }
        public String getUptime() { return uptime; }
        public String getLastCheck() { return lastCheck; }
        public int getProductCount() { return productCount; }
        public int getDiscordUsers() { return discordUsers; }
        public int getChangesToday() { return changesToday; }
        public String getCheckInterval() { return checkInterval; }
        public int getGuildCount() { return guildCount; }
        public String getPrefix() { return prefix; }
    }
}
