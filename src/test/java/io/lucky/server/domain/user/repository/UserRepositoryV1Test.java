package io.lucky.server.domain.user.repository;

import io.lucky.server.common.Configure;
import io.lucky.server.domain.user.entity.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;
import java.util.NoSuchElementException;

class UserRepositoryV1Test {

    private Configure conf;

    private UserRepositoryV1 repository;

    @BeforeEach
    void beforeEach(){
        conf = Configure.getInstance();
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(conf.db_url);
        dataSource.setUsername(conf.db_username);
        dataSource.setPassword(conf.db_password);
        this.repository = new UserRepositoryV1(dataSource);
    }

    @Test
    public void crud() throws SQLException {
        User userA = new User(1L, "userA", 1000);
        User userB = new User(2L, "userB", 1000);
        repository.save(userA);
        repository.save(userB);

        User findUserA = repository.findById(userA.getId());
        User findUserB = repository.findById(userB.getId());

        Assertions.assertThat(userA).isEqualTo(findUserA);
        Assertions.assertThat(userB).isEqualTo(findUserB);

        repository.updateMoney(userA.getId(), -2000);
        repository.updateMoney(userB.getId(), +2000);

        repository.delete(userA.getId());
        repository.delete(userB.getId());

        Assertions.assertThatThrownBy(() -> repository.findById(userA.getId()))
                .isInstanceOf(NoSuchElementException.class);
        Assertions.assertThatThrownBy(() -> repository.findById(userB.getId()))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    public void exist() throws SQLException {
        // 존재하지 않음
        Assertions.assertThat(repository.existsById(1L)).isFalse();

        // 저장한 뒤에는 존재함
        User userA = new User(1L, "userA", 1000);
        repository.save(userA);
        Assertions.assertThat(repository.existsById(userA.getId())).isTrue();

        // 다시 삭제하면 존재하지 않음
        repository.delete(userA.getId());
        Assertions.assertThat(repository.existsById(1L)).isFalse();
    }

}