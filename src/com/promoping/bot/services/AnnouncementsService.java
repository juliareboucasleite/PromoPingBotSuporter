package com.promoping.bot.services;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.promoping.bot.dao.WebhookConfigDAO;
import com.promoping.bot.utils.EmbedBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class AnnouncementsService {
    
    private static final Logger logger = LoggerFactory.getLogger(AnnouncementsService.class);
    private static final String ANNOUNCEMENTS_CHANNEL_ID = "1442931993888428143";
    private final WebhookConfigDAO webhookConfigDAO;

    public AnnouncementsService(WebhookConfigDAO webhookConfigDAO) {
        this.webhookConfigDAO = webhookConfigDAO;
    }
    public WebhookConfigDAO.WebhookConfig getConfig() {
        try {
            return webhookConfigDAO.getConfig("github");
        } catch (Exception e) {
            logger.error("Error getting webhook configuration", e);
            return null;
        }
    }
    
    public void saveConfig(String webhookUrl) {
        try {
            webhookConfigDAO.createOrUpdate("github", webhookUrl);
        } catch (Exception e) {
            logger.error("Error saving webhook configuration", e);
            throw new RuntimeException("Error saving configuration", e);
        }
    }

    public TextChannel getAnnouncementsChannel(JDA jda) {
        return jda.getTextChannelById(ANNOUNCEMENTS_CHANNEL_ID);
    }
    
    public void sendTestNotification(JDA jda) {
        TextChannel channel = getAnnouncementsChannel(jda);
        if (channel == null) {
            throw new RuntimeException("Announcements channel not found");
        }
        
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("New Release - TEST")
                .setDescription("**v2.4.0** has been released!")
                .addField("Repository", "[PromoPing](https://github.com/seu-usuario/PromoPing)", true)
                .addField("Tag", "v2.4.0", true)
                .addField("Author", "Test", true)
                .addField("Release Notes", "This is a test notification to verify the announcements system.", false)
                .setColor(0xf4af55)
                .setThumbnail("https://github.githubassets.com/images/modules/logos_page/GitHub-Mark.png")
                .setTimestamp()
                .setFooter("PromoPing - GitHub Releases");
        
        channel.sendMessageEmbeds(embed.build()).queue();
    }
    
    public SyncResult syncReleases() {
        HttpURLConnection connection = null;
        try {
            String apiUrl = System.getenv("API_URL");
            if (apiUrl == null || apiUrl.isEmpty()) {
                apiUrl = "http://localhost:3000";
            }
            
            URL url = new URL(apiUrl + "/api/webhooks/github/sync");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8")) {
                writer.write("{}");
                writer.flush();
            }
            
            int statusCode = connection.getResponseCode();
            if (statusCode == 200) {
                StringBuilder response = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                }
                String body = response.toString();
                return new SyncResult(true, 0, 0, 0, null);
            } else {
                return new SyncResult(false, 0, 0, 0, "API error: " + statusCode);
            }
            
        } catch (IOException e) {
            logger.error("Error syncing releases", e);
            return new SyncResult(false, 0, 0, 0, e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    
    public static class SyncResult {
        private final boolean success;
        private final int total;
        private final int sent;
        private final int skipped;
        private final String error;
        
        public SyncResult(boolean success, int total, int sent, int skipped, String error) {
            this.success = success;
            this.total = total;
            this.sent = sent;
            this.skipped = skipped;
            this.error = error;
        }
        
        public boolean isSuccess() { return success; }
        public int getTotal() { return total; }
        public int getSent() { return sent; }
        public int getSkipped() { return skipped; }
        public String getError() { return error; }
    }
}
