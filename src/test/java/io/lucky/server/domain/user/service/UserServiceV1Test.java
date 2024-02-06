package io.lucky.server.domain.user.service;

import io.lucky.server.common.Configure;
import io.lucky.server.domain.user.entity.User;
import io.lucky.server.domain.user.repository.UserRepositoryV1;
import io.lucky.server.domain.user.repository.UserRepositoryV1Impl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;
import java.util.NoSuchElementException;

class UserServiceV1Test {

    private Configure conf;

    private UserRepositoryV1 userRepository;
    private UserServiceV1Impl userServiceV1;

    @BeforeEach
    private void beforeEach(){
        conf = Configure.getInstance();
        DriverManagerDataSource dataSource = new DriverManagerDataSource(conf.db_url, conf.db_username, conf.db_password);
        this.userRepository = new UserRepositoryV1Impl(dataSource);
        this.userServiceV1 = new UserServiceV1Impl(userRepository);
    }

    @Test
    public void testTransferMoney() throws SQLException {
        // given
        User userA = new User(1L, "userA", 10000);
        User userB = new User(2L, "userB", 10000);
        userRepository.save(userA);
        userRepository.save(userB);
        //when
        userServiceV1.transferMoney(userA.getId(), userB.getId(), 5000);
        //then
        User findUserA = userRepository.findById(userA.getId());
        User findUserB = userRepository.findById(userB.getId());
        Assertions.assertThat(findUserA.getMoney()).isEqualTo(5000);
        Assertions.assertThat(findUserB.getMoney()).isEqualTo(15000);

        userRepository.delete(userA.getId());
        userRepository.delete(userB.getId());
    }

    @Test
    public void testTransferMoneyFail() throws SQLException {
        // not exist user
        Assertions.assertThatThrownBy(() -> userServiceV1.transferMoney(1L, 2L, 1000))
                .isInstanceOf(NoSuchElementException.class);


        // 한쪽만 존재하는 사용자
        User userA = new User(1L, "userA", 10000);
        userRepository.save(userA);
        Assertions.assertThatThrownBy(() -> userServiceV1.transferMoney(1L, 2L, 1000))
                .isInstanceOf(NoSuchElementException.class);
        Assertions.assertThatThrownBy(() -> userServiceV1.transferMoney(2L, 1L, 1000))
                .isInstanceOf(NoSuchElementException.class);

        // 같은 사용자 사이의 이체
        Assertions.assertThatThrownBy(() -> userServiceV1.transferMoney(1L, 1L, 1000))
                .isInstanceOf(IllegalStateException.class);

        // minus money
        Assertions.assertThatThrownBy(() -> userServiceV1.transferMoney(1L, 2L, -1000))
                .isInstanceOf(IllegalStateException.class);

        // 잔액 부족
        User userB = new User(2L, "userB", 10000);
        userRepository.save(userB);
        Assertions.assertThatThrownBy(() -> userServiceV1.transferMoney(1L, 2L, 100000))
                .isInstanceOf(IllegalStateException.class);

        // 정리
        userRepository.delete(userA.getId());
        userRepository.delete(userB.getId());
    }

}