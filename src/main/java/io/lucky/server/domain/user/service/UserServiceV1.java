package io.lucky.server.domain.user.service;

import io.lucky.server.domain.user.entity.User;

import java.sql.SQLException;

public interface UserServiceV1 {

    public void transferMoney(Long fromId, Long toId, int money) throws SQLException;
}
