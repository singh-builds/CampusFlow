package com.campusflow.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** JDBC: loads db.properties once and gives out MySQL connections. */
public final class DBConnection {
    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) throw new IllegalStateException("db.properties not found. Create config/db.properties and rebuild.");
            PROPS.load(in);
            Class.forName(PROPS.getProperty("db.driver", "com.mysql.cj.jdbc.Driver")); // MySQL Connector/J
        } catch (Exception e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private DBConnection() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(PROPS.getProperty("db.url"), PROPS.getProperty("db.user"), PROPS.getProperty("db.password"));
    }

    public static String get(String key, String defaultValue) {
        return PROPS.getProperty(key, defaultValue);
    }
}
