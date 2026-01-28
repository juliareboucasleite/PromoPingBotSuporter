package com.promoping.bot.services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.InputStream;

public class Database {

    private static String url;
    private static String user;
    private static String password;

    static {
        try {
            Properties props = new Properties();
            try (InputStream in = new FileInputStream("config.properties")) {
                props.load(in);
            }

            url = props.getProperty("DB_URL");
            user = props.getProperty("DB_USER");
            password = props.getProperty("DB_PASSWORD");

            Class.forName("com.mysql.cj.jdbc.Driver");

        } catch (Exception e) {
            throw new RuntimeException("Erro ao iniciar a base de dados", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }
}
