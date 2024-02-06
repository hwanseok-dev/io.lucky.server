package io.lucky.server.domain.user.service;

import io.lucky.server.domain.user.entity.User;
import io.lucky.server.domain.user.repository.UserRepositoryV2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@Service
public class UserServiceV2Impl implements UserServiceV2 {

    private final DataSource dataSource;

    private final UserRepositoryV2 userRepository;

    public UserServiceV2Impl(DataSource dataSource, UserRepositoryV2 userRepository) {
        this.dataSource = dataSource;
        this.userRepository = userRepository;
    }

    @Override
    public User save(Long id, String username, int money) throws SQLException {
        Connection conn = dataSource.getConnection();
        try {
            conn.setAutoCommit(false);
            User user = new User(id, username, money);
            userRepository.save(conn, user);
            conn.commit();
            return user;
        } catch (Exception e) {
            conn.rollback();
            throw new IllegalStateException(e);
        } finally {
            release(conn);
        }
    }

    @Override
    public void transferMoney(Long fromId, Long toId, int money) throws SQLException {
        Connection conn = dataSource.getConnection();
        try {
            conn.setAutoCommit(false);
            businessLogic(fromId, toId, money, conn);
            conn.commit();
        } catch (Exception e) {
            conn.rollback();
            throw new IllegalStateException(e);
        } finally {
            release(conn);
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

    private void businessLogic(Long fromId, Long toId, int money, Connection conn) throws SQLException {
        User fromUser = userRepository.findById(conn, fromId);
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
        User toUser = userRepository.findById(conn, toId);
        userRepository.updateMoney(conn, fromId, fromUser.getMoney() - money);
        userRepository.updateMoney(conn, toId, toUser.getMoney() + money);
    }
}
