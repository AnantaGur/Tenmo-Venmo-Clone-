package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.TransferDTO;
import com.techelevator.tenmo.model.UserDTO;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.math.BigDecimal;

public interface BalanceDao {
    int findByUsername(String username);

    BigDecimal getBalance(int userId);

    String transfer(BigDecimal amount, int fromId, int toId);

}
