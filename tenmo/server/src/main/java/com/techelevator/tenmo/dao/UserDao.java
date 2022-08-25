package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserDTO;

import java.util.List;

public interface UserDao {

    List<User> findAll();

    List<UserDTO> findAllUserNames();

    User findByUsername(String username);

    int findIdByUsername(String username);

    String findUserNameById(int userId);

    boolean create(String username, String password);
}
