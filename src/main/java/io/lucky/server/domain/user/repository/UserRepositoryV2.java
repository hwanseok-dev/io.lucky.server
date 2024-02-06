package io.lucky.server.domain.user.repository;

import io.lucky.server.domain.user.entity.User;

import java.sql.Connection;
import java.sql.SQLException;

public interface UserRepositoryV2 {

    public Long save(Connection conn, User user) throws SQLException;
    public User findById(Connection conn, Long id) throws SQLException;
    public boolean existsById(Connection conn, Long id) throws SQLException;
    public void updateMoney(Connection conn, Long id, int money) throws SQLException;
    void delete(Connection conn, Long id) throws SQLException;
}
