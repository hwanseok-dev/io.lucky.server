package io.lucky.server.domain.user.service;

import io.lucky.server.common.Configure;
import io.lucky.server.domain.user.entity.User;
import io.lucky.server.domain.user.repository.UserRepositoryV3Impl;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.NoSuchElementException;

@Slf4j
@SpringBootTest
class UserServiceV6Test {
    @Autowired
    private UserRepositoryV3Impl userRepository;

    @Autowired
    private UserServiceV6Impl userServiceV6;
 
    @TestConfiguration
    static class TestConfig {
        @Bean
        DataSource dataSource(){
            Configure conf = Configure.getInstance();
            return new DriverManagerDataSource(conf.db_url, conf.db_username, conf.db_password);
        }

        @Bean
        PlatformTransactionManager transactionManager(){
            return new DataSourceTransactionManager(dataSource());
        }

        @Bean
        UserRepositoryV3Impl userRepositoryV3(){
            return new UserRepositoryV3Impl(dataSource());
        }

        @Bean
        UserServiceV6Impl userServiceV6(){
            return new UserServiceV6Impl(userRepositoryV3());
        }
    }

    @Test
    public void testTransferMoney() throws SQLException {
        // given
        User userA = new User(1L, "userA", 10000);
        User userB = new User(2L, "userB", 10000);
        userRepository.save(userA);
        userRepository.save(userB);
        //when
        userServiceV6.transferMoney(userA.getId(), userB.getId(), 5000);
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
        // UnCheckedException의 경우 transactionTemplate에서도 catch가 안되어서 관리가 잘 안되네
        Assertions.assertThatThrownBy(() -> userServiceV6.transferMoney(1L, 2L, 1000))
                .isInstanceOf(NoSuchElementException.class);


        // 한쪽만 존재하는 사용자
        User userA = new User(1L, "userA", 10000);
        userRepository.save(userA);
        Assertions.assertThatThrownBy(() -> userServiceV6.transferMoney(1L, 2L, 1000))
                .isInstanceOf(NoSuchElementException.class);
        Assertions.assertThatThrownBy(() -> userServiceV6.transferMoney(2L, 1L, 1000))
                .isInstanceOf(NoSuchElementException.class);

        // 같은 사용자 사이의 이체
        Assertions.assertThatThrownBy(() -> userServiceV6.transferMoney(1L, 1L, 1000))
                .isInstanceOf(IllegalStateException.class);

        // minus money
        Assertions.assertThatThrownBy(() -> userServiceV6.transferMoney(1L, 2L, -1000))
                .isInstanceOf(IllegalStateException.class);

        // 잔액 부족
        User userB = new User(2L, "userB", 10000);
        userRepository.save(userB);
        Assertions.assertThatThrownBy(() -> userServiceV6.transferMoney(1L, 2L, 100000))
                .isInstanceOf(IllegalStateException.class);
        // 정리
        userRepository.delete(userA.getId());
        userRepository.delete(userB.getId());
    }

    @Test
    public void testTransactionFailRollBack() throws SQLException {
        User userA = new User(3L, "userA", 1000);
        User userB = new User(4L, "userB", 1000);
        userRepository.save(userA);
        userRepository.save(userB);

        Assertions.assertThatThrownBy(() ->
                        userServiceV6.transferMoney(userA.getId(), userB.getId(), userA.getMoney()))
                .isInstanceOf(IllegalStateException.class);

        User findUserA = userRepository.findById(userA.getId());
        User findUserB = userRepository.findById(userB.getId());
        Assertions.assertThat(findUserA.getMoney()).isEqualTo(1000);
        Assertions.assertThat(findUserB.getMoney()).isEqualTo(1000);

        userRepository.delete(userA.getId());
        userRepository.delete(userB.getId());
    }


    @Test
    public void testTransferFailRollback() throws SQLException {
        // given
        /**
         * Repository 계층에서는 트랜잭션 롤백 기능이 없다
         * Repository 계층에서 autocommit false도 안되어 있다
         */
//        userRepository.save(userA);
//        userRepository.save(userB);
        User userA = userServiceV6.save(1L, "userA", 10000);
        User userB = userServiceV6.save(2L, "userB", 10000);
        //when
        Assertions.assertThatThrownBy(() -> userServiceV6.transferMoney(userA.getId(), userB.getId(), 100000))
                .isInstanceOf(IllegalStateException.class);

        //then
        /**
         * Service 계층에 save를 하는 메서드를 만들어도, service 계층의 save와 transferMoney를 하나의 트랜잭션으로 묶는 과정이 어렵다
         * userRepository.save 메서드는 같은 트랜잭션으로 처리되지 못해서 아래의 userA, userB 데이터를 저장되어 있다
         */
//        Assertions.assertThatThrownBy(() -> userRepository.findById(userA.getId()))
//                .isInstanceOf(IllegalStateException.class);
//        Assertions.assertThatThrownBy(() -> userRepository.findById(userB.getId()))
//                .isInstanceOf(IllegalStateException.class);

        /**
         * service 계층의 트랜잭션 전파가 되지 않아서 객체 저장 시점에 commit이 되어버려 수동으로 삭제해주어야 한다
         */
        userRepository.delete(userA.getId());
        userRepository.delete(userB.getId());
    }

}