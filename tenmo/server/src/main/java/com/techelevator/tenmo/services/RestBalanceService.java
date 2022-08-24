package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.BalanceDTO;
import com.techelevator.tenmo.model.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class RestBalanceService implements BalanceService{
    JdbcTemplate jdbcTemplate;
    BalanceDTO balanceDTO;
    private final String API_URL = "http://localhost:8080/balance";

    @Override
    public int findByUsername(String username) {
        String sql = "SELECT user_id, username FROM tenmo_user WHERE username ILIKE ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, username);
        if (rowSet.next()){
            return mapRowToUser(rowSet).getId();
        }
        throw new UsernameNotFoundException("User " + username + " was not found.");
    }

    public List<BigDecimal> getBalance(int userId) throws UsernameNotFoundException {
        List<BigDecimal> balanceDTOList = new ArrayList<>();
        String sql = "SELECT balance FROM account WHERE user_id = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, userId);
        while (rowSet.next()){
            balanceDTOList.add(balanceDTO.getBalance());
        }
        return balanceDTOList;
    }


    private User mapRowToUser(SqlRowSet rs) {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        return user;
    }
}
