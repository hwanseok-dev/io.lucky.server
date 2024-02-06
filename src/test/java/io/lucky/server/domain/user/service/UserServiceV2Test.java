package io.lucky.server.domain.user.service;

import io.lucky.server.common.Configure;
import io.lucky.server.domain.user.entity.User;
import io.lucky.server.domain.user.repository.UserRepositoryV2;
import io.lucky.server.domain.user.repository.UserRepositoryV2Impl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

class UserServiceV2Test {

    private Configure conf;


    private DataSource dataSource;
    private UserRepositoryV2 userRepository;
    private UserServiceV2Impl userServiceV2;

    @BeforeEach
    private void beforeEach(){
        conf = Configure.getInstance();
        this.dataSource = new DriverManagerDataSource(conf.db_url, conf.db_username, conf.db_password);
        this.userRepository = new UserRepositoryV2Impl(dataSource);
        this.userServiceV2 = new UserServiceV2Impl(dataSource, userRepository);
    }

    @Test
    public void testTransferMoney() throws SQLException {
        Connection conn = dataSource.getConnection();

        // given
        User userA = new User(1L, "userA", 10000);
        User userB = new User(2L, "userB", 10000);
        userRepository.save(conn, userA);
        userRepository.save(conn, userB);
        //when
        userServiceV2.transferMoney(userA.getId(), userB.getId(), 5000);
        //then
        User findUserA = userRepository.findById(conn, userA.getId());
        User findUserB = userRepository.findById(conn, userB.getId());
        Assertions.assertThat(findUserA.getMoney()).isEqualTo(5000);
        Assertions.assertThat(findUserB.getMoney()).isEqualTo(15000);

        userRepository.delete(conn, userA.getId());
        userRepository.delete(conn, userB.getId());
    }

    @Test
    public void testTransferMoneyFail() throws SQLException {
        Connection conn = dataSource.getConnection();

        // not exist user
        Assertions.assertThatThrownBy(() -> userServiceV2.transferMoney(1L, 2L, 1000))
                .isInstanceOf(IllegalStateException.class);


        // 한쪽만 존재하는 사용자
        User userA = new User(1L, "userA", 10000);
        userRepository.save(conn, userA);
        Assertions.assertThatThrownBy(() -> userServiceV2.transferMoney(1L, 2L, 1000))
                .isInstanceOf(IllegalStateException.class);
        Assertions.assertThatThrownBy(() -> userServiceV2.transferMoney(2L, 1L, 1000))
                .isInstanceOf(IllegalStateException.class);

        // 같은 사용자 사이의 이체
        Assertions.assertThatThrownBy(() -> userServiceV2.transferMoney(1L, 1L, 1000))
                .isInstanceOf(IllegalStateException.class);

        // minus money
        Assertions.assertThatThrownBy(() -> userServiceV2.transferMoney(1L, 2L, -1000))
                .isInstanceOf(IllegalStateException.class);

        // 잔액 부족
        User userB = new User(2L, "userB", 10000);
        userRepository.save(conn, userB);
        Assertions.assertThatThrownBy(() -> userServiceV2.transferMoney(1L, 2L, 100000))
                .isInstanceOf(IllegalStateException.class);

        // 정리
        userRepository.delete(conn, userA.getId());
        userRepository.delete(conn, userB.getId());
    }


    @Test
    public void testTransferFailRollback() throws SQLException {
        Connection conn = dataSource.getConnection();

        // given
        /**
         * Repository 계층에서는 트랜잭션 롤백 기능이 없다
         * Repository 계층에서 autocommit false도 안되어 있다
         */
//        userRepository.save(conn, userA);
//        userRepository.save(conn, userB);
        User userA = userServiceV2.save(1L, "userA", 10000);
        User userB = userServiceV2.save(2L, "userB", 10000);
        //when
        Assertions.assertThatThrownBy(() -> userServiceV2.transferMoney(userA.getId(), userB.getId(), 100000))
                .isInstanceOf(IllegalStateException.class);

        //then
        /**
         * Service 계층에 save를 하는 메서드를 만들어도, service 계층의 save와 transferMoney를 하나의 트랜잭션으로 묶는 과정이 어렵다
         * userRepository.save 메서드는 같은 트랜잭션으로 처리되지 못해서 아래의 userA, userB 데이터를 저장되어 있다
         */
//        Assertions.assertThatThrownBy(() -> userRepository.findById(conn, userA.getId()))
//                .isInstanceOf(IllegalStateException.class);
//        Assertions.assertThatThrownBy(() -> userRepository.findById(conn, userB.getId()))
//                .isInstanceOf(IllegalStateException.class);

        /**
         * service 계층의 트랜잭션 전파가 되지 않아서 객체 저장 시점에 commit이 되어버려 수동으로 삭제해주어야 한다
         */
        userRepository.delete(conn, userA.getId());
        userRepository.delete(conn, userB.getId());
    }

}