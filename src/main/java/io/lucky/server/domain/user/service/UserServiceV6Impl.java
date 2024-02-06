package io.lucky.server.domain.user.service;

import io.lucky.server.domain.user.entity.User;
import io.lucky.server.domain.user.repository.UserRepositoryV3Impl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.SQLException;

/**
 * 프록시를 사용해서 트랜잭션을 사용하는 객체와
 * 비즈니스 로직을 담당하는 객체를 분리한다
 *
 * 스프링에서 AOP를 사용해서 프록시 객체를 만들어서 트랜잭션 로직을 처리한 뒤,
 * 원래 비즈니스 로직을 대신 호출해준다
 *
 */
@Slf4j
public class UserServiceV6Impl implements UserServiceV2 {

    // DataSource를 서비스 계층에서 주입받지 않고
    // 트랜잭션 매니저를 통해서 커넥션을 가져온다
    // 주입받는 클래스 : DataSourceTransactionManager, JpaTransactionManager

    private final UserRepositoryV3Impl userRepository;

    public UserServiceV6Impl(UserRepositoryV3Impl userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public User save(Long id, String username, int money) throws SQLException {
        User user = new User(id, username, money);
        userRepository.save(user);
        return user;
    }

    @Transactional
    @Override
    public void transferMoney(Long fromId, Long toId, int money) throws SQLException {
        businessLogic(fromId, toId, money);
    }

    private void businessLogic(Long fromId, Long toId, int money) throws SQLException {
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
        if (fromId == 3) {
            throw new IllegalStateException("트랜잭션 중간에 에러가 발생");
        }
        userRepository.updateMoney(toId, toUser.getMoney() + money);
    }
}
