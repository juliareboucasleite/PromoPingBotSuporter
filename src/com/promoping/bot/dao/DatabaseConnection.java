package com.promoping.bot.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Manages MySQL database connections.
 */
public class DatabaseConnection {
    
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);
    
    private static String dbHost;
    private static String dbUser;
    private static String dbPassword;
    private static String dbName;
    private static int dbPort;
    
    static {
        dbHost = System.getenv("DB_HOST");
        if (dbHost == null || dbHost.isEmpty()) {
            dbHost = "localhost";
        }
        
        dbUser = System.getenv("DB_USER");
        if (dbUser == null || dbUser.isEmpty()) {
            dbUser = "root";
        }
        
        dbPassword = System.getenv("DB_PASSWORD");
        if (dbPassword == null) {
            dbPassword = "";
        }
        
        String dbNameEnv = System.getenv("DB_NAME");
        if (dbNameEnv == null || dbNameEnv.isEmpty()) {
            dbName = "pap";
        } else {
            dbName = dbNameEnv;
        }
        
        String dbPortEnv = System.getenv("DB_PORT");
        if (dbPortEnv == null || dbPortEnv.isEmpty()) {
            dbPort = 3306;
        } else {
            try {
                dbPort = Integer.parseInt(dbPortEnv);
            } catch (NumberFormatException e) {
                dbPort = 3306;
            }
        }
    }
    
    /**
     * Gets a new database connection.
     * 
     * @return Connection
     * @throws SQLException If connection error occurs
     */
    public static Connection getConnection() throws SQLException {
        String url = String.format("jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true", 
                dbHost, dbPort, dbName);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL driver not found in classpath", e);
        }

        return DriverManager.getConnection(url, dbUser, dbPassword);
    }
    
    /**
     * Closes a connection safely.
     * 
     * @param connection The connection to close
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.error("Error closing connection", e);
            }
        }
    }
}
