package com.techelevator.dao;

import com.techelevator.tenmo.dao.JdbcBalance;
import com.techelevator.tenmo.dao.JdbcUserDao;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;

public class JdbcBalanceTest extends BaseDaoTests{

    private JdbcBalance sut;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcBalance(jdbcTemplate);
    }

    @Test
    public void return_bob_from_1001(){
        String expected = "bob";
        String actual = sut.findUserById(1001);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void return_user_from_1002(){
        String expected = "user";
        String actual = sut.findUserById(1002);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void return_1003_from_1001(){
        BigDecimal expected = new BigDecimal("1003.00");
        BigDecimal actual = sut.getBalance(1001);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void return_99999_from_1002(){
        BigDecimal expected = new BigDecimal("9999.99");
        BigDecimal actual = sut.getBalance(1002);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void return_accepted(){
        String expected = "Accepted";
        String hash = sut.;
        String actual = sut.transfer(new BigDecimal("10.00"), 1001, 1002);
        Assert.assertEquals(expected, actual);
    }

}