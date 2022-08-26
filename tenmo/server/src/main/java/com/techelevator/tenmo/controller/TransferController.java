package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.JdbcBalance;
import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.model.DetailDTO;
import com.techelevator.tenmo.model.TransferDTO;
import com.techelevator.tenmo.model.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class TransferController {
    @Autowired
    JdbcBalance jdbcBalance;
    @Autowired
    JdbcUserDao jdbcUserDao;

    @RequestMapping(value = "/balance", method = RequestMethod.GET)
    public BigDecimal getBalance(Principal principal) {
        String userName = principal.getName();
        int userId = jdbcUserDao.findIdByUsername(userName);
        BigDecimal balance = jdbcBalance.getBalance(userId);
        return balance;
    }

    @RequestMapping(value = "/transfer")
    public String transfer(Principal principal, @RequestBody TransferDTO transferDTO) {
        int principalId = jdbcUserDao.findIdByUsername(principal.getName());
        int fromId = transferDTO.getFromId();
        if (principalId == fromId) {
            return jdbcBalance.transfer(transferDTO.getAmount(),
                    transferDTO.getFromId(), transferDTO.getToId());
        }
        return "REJECTED";
    }

    @RequestMapping(value = "/listUsers", method = RequestMethod.GET)
    public List<UserDTO> listAllUsers(){
        return jdbcUserDao.findAllUserNames();
    }

    @RequestMapping(value = "/listTransactions", method = RequestMethod.GET)
    public List<TransferDTO> listOfTransactions(Principal principal){
        int principalId = jdbcUserDao.findIdByUsername(principal.getName());
        return jdbcBalance.getTransfersById(principalId);
    }

    @RequestMapping(value = "/transaction", method = RequestMethod.GET)
    public DetailDTO transactionById(Principal principal, @RequestBody DetailDTO detailDTO){
        System.out.println(detailDTO.getTransferId());

        int principalId = jdbcUserDao.findIdByUsername(principal.getName());
        int fromId = jdbcUserDao.findIdByUsername(principal.getName());

        if (principalId == fromId) {
            detailDTO = jdbcBalance.getTransferById(detailDTO.getTransferId(), principal.getName());
        }

        return detailDTO;
    }
}
