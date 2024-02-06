package io.lucky.server.domain.user.service;

import io.lucky.server.domain.user.entity.User;
import io.lucky.server.domain.user.repository.UserRepositoryV1;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Slf4j
@Service
public class UserServiceV1Impl implements UserServiceV1 {
    private final UserRepositoryV1 userRepository;

    public UserServiceV1Impl(UserRepositoryV1 userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void transferMoney(Long fromId, Long toId, int money) throws SQLException {
        User fromUser = userRepository.findById(fromId);
        if (money <= 0) {
            throw new IllegalStateException("money must be greater then then 0");
        }
        if (fromUser.getMoney() < money) {
            log.error("transfer error. fromId : {}, fromMoney : {}, transferMoney : {}", fromId, fromUser.getMoney(), money);
            throw new IllegalStateException("fromUser does not have enough money");
        }
        if (fromId == toId) {
            throw new IllegalStateException("fromId and toId must be not equal");
        }
        User toUser = userRepository.findById(toId);
        userRepository.updateMoney(fromId, fromUser.getMoney() - money);
        userRepository.updateMoney(toId, toUser.getMoney() + money);
    }
}
