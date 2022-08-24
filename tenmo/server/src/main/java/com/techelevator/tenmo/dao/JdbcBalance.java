package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.User;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.xml.crypto.Data;
import java.math.BigDecimal;


@Component
public class JdbcBalance implements BalanceDao {
    JdbcTemplate jdbcTemplate ;

    public JdbcBalance(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int findByUsername(String username) {
        String sql = "SELECT user_id, username FROM tenmo_user WHERE username ILIKE ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, username);
        if (rowSet.next()){
            return rowSet.getInt("user_id");
        }
        throw new UsernameNotFoundException("User " + username + " was not found.");
    }

    @Override
    public BigDecimal getBalance(int userId) throws UsernameNotFoundException {
        BigDecimal balance = new BigDecimal("-1");
        String sql = "SELECT balance FROM account WHERE user_id = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, userId);
        if (rowSet.next()){
            balance = rowSet.getBigDecimal("balance");
        }
        return balance;
    }

    @Override
    public boolean transfer(BigDecimal amount, int fromId, int toId) {
        String sql = "UPDATE transfer SET balance = ? WHERE account_id_from = ?";
        String sql2 = "UPDATE transfer SET balance = ? WHERE account_id_to = ? ";
        try{
            jdbcTemplate.update(sql, getBalance(fromId), fromId, toId);
        } catch (DataAccessException e){
            return false;
        }
        return true;
    }




    private User mapRowToUser(SqlRowSet rs) {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        return user;
    }
}
