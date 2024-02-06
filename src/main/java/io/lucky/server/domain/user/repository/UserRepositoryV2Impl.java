package io.lucky.server.domain.user.repository;

import io.lucky.server.domain.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

import static io.lucky.server.domain.user.config.DBConnection.getConnection;

@Slf4j
public class UserRepositoryV2Impl implements UserRepositoryV2 {

    private final DataSource dataSource;

    public UserRepositoryV2Impl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Long save(Connection conn, User user) throws SQLException {
        String sql = "insert into user (id, name, money) values (?, ?, ?)";
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
            close(pstmt, null);
        }
    }

    @Override
    public User findById(Connection conn, Long id) throws SQLException {
        String sql = "select * from user where id = ?";
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
            close(pstmt, rs);
        }
    }

    @Override
    public boolean existsById(Connection conn, Long id) throws SQLException {
        String sql = "select count(*) as count from user where id = ?";
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
            close(pstmt, null);
        }
    }

    @Override
    public void updateMoney(Connection conn, Long id, int money) throws SQLException {
        String sql = "update user set money = ? where id = ?";
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
            close(pstmt, null);
        }
    }

    @Override
    public void delete(Connection conn, Long id) throws SQLException {
        String sql = "delete from user where id = ?";
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
            close(pstmt, null);
        }
    }

    private void close(Statement stmt, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
    }
}
