package io.lucky.server.domain.user.repository;

import io.lucky.server.domain.user.entity.User;

import java.sql.SQLException;

public interface UserRepositoryV1 {

    public Long save(User user) throws SQLException;
    public User findById(Long id) throws SQLException;
    public boolean existsById(Long id) throws SQLException;
    public void updateMoney(Long id, int money) throws SQLException;
    void delete(Long id) throws SQLException;
}
