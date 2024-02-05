package io.lucky.server.domain.user.config;

import io.lucky.server.common.Configure;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
public class DBConnection {

    private static final Configure conf = Configure.getInstance();
    private DBConnection(){}
    public static Connection getConnection(){
        try {
            Connection connection = DriverManager.getConnection(conf.db_url, conf.db_username, conf.db_password);
            log.info("get connection : {}, class : {}", connection, connection.getClass());
            return connection;
        } catch (SQLException e) {
            throw new IllegalStateException("db connection error", e);
        }
    }
}
