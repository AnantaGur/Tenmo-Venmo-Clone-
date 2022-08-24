package com.techelevator.tenmo.services;

import java.math.BigDecimal;
import java.util.List;

public interface BalanceService {
    int findByUsername(String username);
    List<BigDecimal> getBalance(int userId);
}
