package com.techelevator.tenmo.model;

import org.springframework.jdbc.support.rowset.SqlRowSet;

public class UserDTO {

    private int userId;
    private String userName;

    public UserDTO(int userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    public UserDTO(){

    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                '}';
    }
}
