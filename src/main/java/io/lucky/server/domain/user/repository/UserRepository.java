package io.lucky.server.domain.user.repository;

import io.lucky.server.domain.user.entity.User;

import java.sql.SQLException;

public interface UserRepository {

    public Long save(User user) throws SQLException;
    public User findById(Long id) throws SQLException;
    public void updateMoney(Long id, int money) throws SQLException;
}
