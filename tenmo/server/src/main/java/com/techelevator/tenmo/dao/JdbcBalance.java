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
    JdbcTemplate jdbcTemplate;

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
    public String transfer(BigDecimal amount, int fromId, int toId) {
        BigDecimal fromBalance = getBalance(fromId);
        BigDecimal toBalance = getBalance(toId);
        if (getBalance(fromId).compareTo(BigDecimal.ZERO) > 0 &&
        getBalance(fromId).compareTo(amount) > 0 && fromId != toId) {
            fromBalance = fromBalance.subtract(amount);
            toBalance = getBalance(toId).add(amount);
        }
        String sql = "UPDATE account SET balance = ? WHERE user_id = ?";
        String sql2 = "UPDATE account SET balance = ? WHERE user_id = ? ";
        String sql3 = "INSERT INTO transfer (user_id_from, user_id_to, amount, status) " +
                "VALUES (?, ?, ?, 'APPROVED')";
        try{
            jdbcTemplate.update(sql, fromBalance, fromId);
            jdbcTemplate.update(sql2, toBalance, toId);
            jdbcTemplate.update(sql3, fromId, toId, amount);
        } catch (DataAccessException e){
            return "Rejected";
        }

        return "Approved";
    }
}
