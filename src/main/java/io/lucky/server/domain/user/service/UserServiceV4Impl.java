package io.lucky.server.domain.user.service;

import io.lucky.server.domain.user.entity.User;
import io.lucky.server.domain.user.repository.UserRepositoryV3Impl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@Service
public class UserServiceV4Impl implements UserServiceV2 {

    // DataSource를 서비스 계층에서 주입받지 않고
    // 트랜잭션 매니저를 통해서 커넥션을 가져온다
    // 주입받는 클래스 : DataSourceTransactionManager, JpaTransactionManager

    private final TransactionTemplate transactionTemplate;
    private final UserRepositoryV3Impl userRepository;

    public UserServiceV4Impl(PlatformTransactionManager transactionManager, UserRepositoryV3Impl userRepository) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.userRepository = userRepository;
    }

    @Override
    public User save(Long id, String username, int money) throws SQLException {
        return transactionTemplate.execute((status) -> {
            try {
                User user = new User(id, username, money);
                userRepository.save(user);
                return user;
            } catch (SQLException e) {
                throw new IllegalStateException();
            }
        });
    }

    @Override
    public void transferMoney(Long fromId, Long toId, int money) throws SQLException {
        /**
         * 비즈니스 로직이 정상 수행되면 커밋
         * 언체크 예외가 발생하면 롤백
         * 체크 예외가 발생하면 커밋 -> ????
         */
        transactionTemplate.executeWithoutResult(status -> {
            try {
                businessLogic(fromId, toId, money);
            } catch (SQLException e) {
                // 언체크 예외로 변경해서 던지고 롤백을 유도함
                throw new IllegalStateException();
            }
        });
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
