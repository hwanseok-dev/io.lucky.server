package io.lucky.server.domain.user.service;

import java.sql.SQLException;

public interface UserService {

    public void transferMoney(Long fromId, Long toId, int money) throws SQLException;
}
