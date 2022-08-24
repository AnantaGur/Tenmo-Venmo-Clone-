package com.techelevator.tenmo.model;

import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;


public class BalanceDTO {



    private int userId;
    private BigDecimal balance;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
