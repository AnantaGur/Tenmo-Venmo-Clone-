package com.techelevator.tenmo.dao;

import java.math.BigDecimal;

public interface BalanceDao {
    int findByUsername(String username);

    BigDecimal getBalance(int userId);

    boolean transfer(BigDecimal amount, int fromId, int toId);

}
