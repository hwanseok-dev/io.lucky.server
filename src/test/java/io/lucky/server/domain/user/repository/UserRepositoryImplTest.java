package io.lucky.server.domain.user.repository;

import io.lucky.server.common.Configure;
import io.lucky.server.domain.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class UserRepositoryImplTest {

    private final Configure conf = Configure.getInstance();

    private UserRepositoryImpl repository;

    @BeforeEach
    void beforeEach(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(conf.db_url);
        dataSource.setUsername(conf.db_username);
        dataSource.setPassword(conf.db_password);
        this.repository = new UserRepositoryImpl(dataSource);
    }

    @Test
    public void crud() throws SQLException {
        User userA = new User(1L, "userA", 1000);
        User userB = new User(2L, "userB", 1000);
        repository.save(userA);
        repository.save(userB);


    }

}