package io.lucky.server.domain.user.service;

import io.lucky.server.domain.user.entity.User;
import io.lucky.server.domain.user.repository.UserRepositoryV2;
import io.lucky.server.domain.user.repository.UserRepositoryV3Impl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceV3Impl implements UserServiceV2 {

    // DataSource를 서비스 계층에서 주입받지 않고
    // 트랜잭션 매니저를 통해서 커넥션을 가져온다
    // 주입받는 클래스 : DataSourceTransactionManager, JpaTransactionManager
    private final PlatformTransactionManager transactionManager;

    private final UserRepositoryV3Impl userRepository;

    @Override
    public User save(Long id, String username, int money) throws SQLException {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            User user = new User(id, username, money);
            userRepository.save(user);
            transactionManager.commit(status);
            return user;
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new IllegalStateException(e);
        }
        // 릴리즈는 직접하지 않고 트랜잭션 매니저에서 담당한다
    }

    @Override
    public void transferMoney(Long fromId, Long toId, int money) throws SQLException {
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            businessLogic(fromId, toId, money);
            transactionManager.commit(status);
        } catch (Exception e) {
            transactionManager.rollback(status);
            throw new IllegalStateException(e);
        }
    }

    private static void release(Connection conn) {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (Exception e) {
                log.error("error", e);
            }
        }
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
        userRepository.updateMoney(toId, toUser.getMoney() + money);
    }
}
