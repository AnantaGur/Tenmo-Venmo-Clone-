package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserDTO;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.xml.crypto.Data;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcUserDao implements UserDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcUserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int findIdByUsername(String username) {
        String sql = "SELECT user_id FROM tenmo_user WHERE username ILIKE ?;";
        Integer id = jdbcTemplate.queryForObject(sql, Integer.class, username);
        if (id != null) {
            return id;
        } else {
            return -1;
        }
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT user_id, username, password_hash FROM tenmo_user;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while(results.next()) {
            User user = mapRowToUser(results);
            users.add(user);
        }
        return users;
    }

    @Override
    public User findByUsername(String username) throws UsernameNotFoundException {
        String sql = "SELECT user_id, username, password_hash FROM tenmo_user WHERE username ILIKE ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, username);
        if (rowSet.next()){
            return mapRowToUser(rowSet);
        }
        throw new UsernameNotFoundException("User " + username + " was not found.");
    }

    @Override
    public boolean create(String username, String password) {
        String sql = "INSERT INTO tenmo_user (username, password_hash) VALUES (?, ?) RETURNING user_id";
        String password_hash = new BCryptPasswordEncoder().encode(password);
        Integer newUserId;
        try {
            newUserId = jdbcTemplate.queryForObject(sql, Integer.class, username, password_hash);
            loadNewBalance(newUserId);
        } catch (DataAccessException e) {
            return false;
        }
        // TODO: Create the account record with initial balance
        return true;
    }

    @Override
    public List<UserDTO> findAllUserNames() {
        List<UserDTO> users = new ArrayList<>();
        String sql = "SELECT user_id, username FROM tenmo_user;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while(results.next()) {
            UserDTO user = mapRowToUserDTO(results);
            users.add(user);
        }
        return users;
    }

    @Override
    public String findUserNameById(int userId) {
        String sql = "SELECT username FROM tenmo_user WHERE user_id = ?";
        return jdbcTemplate.queryForObject(sql, String.class, userId);
    }

    @Override
    public void loadNewBalance(int newUserId){
        BigDecimal STARTING_BALANCE = new BigDecimal("1000.00");
        String sql = "INSERT INTO account (user_id, balance) VALUES (?, ?)";
        try {
            jdbcTemplate.update(sql, newUserId, STARTING_BALANCE);
        } catch(DataAccessException ex) {
            System.out.println("DATA ACCESS EXCEPTION");
        }
    }

    private User mapRowToUser(SqlRowSet rs) {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password_hash"));
        user.setActivated(true);
        user.setAuthorities("USER");
        return user;
    }

    private UserDTO mapRowToUserDTO(SqlRowSet rs) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(rs.getInt("user_id"));
        userDTO.setUserName(rs.getString("username"));
        return userDTO;
    }
}
