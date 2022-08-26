package com.techelevator.dao;

import com.techelevator.tenmo.dao.JdbcBalance;
import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.model.DetailDTO;
import com.techelevator.tenmo.model.TransferDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
    public void return_approved_transfer(){
        String expected = "APPROVED";
        String actual = sut.transfer(new BigDecimal("10.00"), 1001, 1002);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void return_rejected_transfer(){
        String expected = "REJECTED";
        String actual = sut.transfer(new BigDecimal("99999.99"), 1002, 1001);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void return_list_of_transfers_by_user_id_1001(){
        sut.transfer(new BigDecimal("100.00"), 1001, 1002);
        sut.transfer(new BigDecimal("200.00"), 1001, 1002);

        TransferDTO transferDTO1 = new TransferDTO(new BigDecimal("100.00"), 1001, 1002, "APPROVED", "SEND");
        TransferDTO transferDTO2 = new TransferDTO(new BigDecimal("200.00"), 1001, 1002, "APPROVED", "SEND");

        List<TransferDTO> expected = new ArrayList<>();
        expected.add(transferDTO1);
        expected.add(transferDTO2);

        List<TransferDTO> actual = sut.getListTransfersById(1001);

        Assert.assertEquals(expected.toString(), actual.toString());
    }

    @Test
    public void return_list_of_transfers_by_user_id_1002(){
        sut.transfer(new BigDecimal("130.00"), 1002, 1001);
        sut.transfer(new BigDecimal("235.00"), 1002, 1001);

        TransferDTO transferDTO1 = new TransferDTO(new BigDecimal("130.00"), 1002, 1001, "APPROVED", "SEND");
        TransferDTO transferDTO2 = new TransferDTO(new BigDecimal("235.00"), 1002, 1001, "APPROVED", "SEND");

        List<TransferDTO> expected = new ArrayList<>();
        expected.add(transferDTO1);
        expected.add(transferDTO2);

        List<TransferDTO> actual = sut.getListTransfersById(1002);

        Assert.assertEquals(expected.toString(), actual.toString());
    }

    @Test
    public void get_transfer_by_id_1002(){
        sut.transfer(new BigDecimal("130.00"), 1002, 1001);

        int expected = 3001;
        DetailDTO testId = sut.getTransferById(3001, "user");
        int actual = testId.getTransferId();

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void get_transfer_by_id_1001_return_0(){
        sut.transfer(new BigDecimal("163.00"), 1001, 1002);

        int expected = 0;
        DetailDTO testId = sut.getTransferById(3010, "bob");
        int actual = testId.getTransferId();

        Assert.assertEquals(expected, actual);
    }

}