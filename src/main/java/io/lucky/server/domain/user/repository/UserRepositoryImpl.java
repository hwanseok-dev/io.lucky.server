package io.lucky.server.domain.user.repository;

import io.lucky.server.domain.user.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

import static io.lucky.server.domain.user.config.DBConnection.getConnection;

@Slf4j
public class UserRepositoryImpl implements UserRepository {

    private final DataSource dataSource;

    public UserRepositoryImpl(DataSource dataSource) {
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

    private void close(Connection conn, Statement stmt, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeStatement(stmt);
        JdbcUtils.closeConnection(conn);
    }
}
