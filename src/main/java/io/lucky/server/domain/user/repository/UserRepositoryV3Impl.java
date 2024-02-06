package io.lucky.server.domain.user.repository;

import io.lucky.server.domain.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

@Slf4j
public class UserRepositoryV3Impl implements UserRepositoryV1 {

    private final DataSource dataSource;

    public UserRepositoryV3Impl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Long save(User user) throws SQLException {
        String sql = "insert into user (id, name, money) values (?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, user.getId());
            pstmt.setString(2, user.getName());
            pstmt.setInt(3, user.getMoney());
            int count = pstmt.executeUpdate();
            return user.getId();
        } catch (SQLException e) {
            log.error("sql error", e);
            throw e;
        } finally {
            close(conn, pstmt, null);
        }
    }

    @Override
    public User findById(Long id) throws SQLException {
        String sql = "select * from user where id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                long userId = rs.getLong("id");
                String userName = rs.getString("name");
                int userMoney = rs.getInt("money");
                User user = new User(userId, userName, userMoney);
                return user;
            }
            throw new NoSuchElementException("user not found. id : " + id);
        } catch (SQLException e) {
            log.error("sql error", e);
            throw e;
        } finally {
            close(conn, pstmt, null);
        }
    }

    @Override
    public boolean existsById(Long id) throws SQLException {
        String sql = "select count(*) as count from user where id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, id);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt("count");
                return count > 0;
            }
            return false;
        } catch (SQLException e) {
            log.error("sql error", e);
            throw e;
        } finally {
            close(conn, pstmt, null);
        }
    }

    @Override
    public void updateMoney(Long id, int money) throws SQLException {
        String sql = "update user set money = ? where id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, money);
            pstmt.setLong(2, id);
            int count = pstmt.executeUpdate();
            log.info("update money. count : {}", count);
        } catch (SQLException e) {
            log.error("sql error", e);
            throw e;
        } finally {
            close(conn, pstmt, null);
        }
    }

    @Override
    public void delete(Long id) throws SQLException {
        String sql = "delete from user where id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, id);
            int count = pstmt.executeUpdate();
            log.info("delete user. id : {}", id);
        } catch (SQLException e) {
            log.error("sql error", e);
            throw e;
        } finally {
            close(conn, pstmt, null);
        }
    }

    private void close(Connection conn, Statement stmt, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        // 트랜잭셔 동기화 매니저에서 릴리즈를 해야한다
        // 트랜잭션 동기화 매니저가 관리하는 커넥션이 아닌 경우 해당 커넥션을 닫는다
        DataSourceUtils.releaseConnection(conn, dataSource);
    }

    private Connection getConnection() throws SQLException {
        // 트랜잭션 동기화 매니저에서 커넥션을 가져온다
        // 트랜잭션 동기화 매니저에서 관리하는 커넥션이 없으면 생성해서 반환한다
        Connection conn = DataSourceUtils.getConnection(dataSource);
        log.info("get conn : {}, class : {}", conn, conn.getClass());
        return conn;
    }
}
