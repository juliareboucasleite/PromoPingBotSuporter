package com.promoping.bot.services;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public final class ReviewSessionStore {

    private static final long MAX_AGE_MS = TimeUnit.MINUTES.toMillis(10);
    private static final Map<String, ReviewSession> sessions = new ConcurrentHashMap<>();

    private ReviewSessionStore() {}

    public static ReviewSession startSession(String userId, String channelId) {
        String key = key(userId, channelId);
        ReviewSession session = new ReviewSession(userId, channelId);
        sessions.put(key, session);
        return session;
    }

    public static ReviewSession getSession(String userId, String channelId) {
        String key = key(userId, channelId);
        ReviewSession session = sessions.get(key);
        if (session == null) {
            return null;
        }
        if (isExpired(session)) {
            sessions.remove(key);
            return null;
        }
        return session;
    }

    public static void clearSession(String userId, String channelId) {
        sessions.remove(key(userId, channelId));
    }

    private static String key(String userId, String channelId) {
        return userId + ":" + channelId;
    }

    private static boolean isExpired(ReviewSession session) {
        return System.currentTimeMillis() - session.getUpdatedAt() > MAX_AGE_MS;
    }

    public static final class ReviewSession {
        private final String userId;
        private final String channelId;
        private String tipo;
        private Boolean anonimo;
        private Integer rating;
        private long updatedAt;

        public ReviewSession(String userId, String channelId) {
            this.userId = userId;
            this.channelId = channelId;
            touch();
        }

        public String getUserId() { return userId; }
        public String getChannelId() { return channelId; }
        public String getTipo() { return tipo; }
        public Boolean getAnonimo() { return anonimo; }
        public Integer getRating() { return rating; }
        public long getUpdatedAt() { return updatedAt; }

        public void setTipo(String tipo) {
            this.tipo = tipo;
            touch();
        }

        public void setAnonimo(Boolean anonimo) {
            this.anonimo = anonimo;
            touch();
        }

        public void setRating(Integer rating) {
            this.rating = rating;
            touch();
        }

        public boolean isReadyForText() {
            return tipo != null && anonimo != null && rating != null;
        }

        private void touch() {
            this.updatedAt = System.currentTimeMillis();
        }
    }
}
