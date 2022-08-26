package com.techelevator.dao;


import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class JdbcUserDaoTests extends BaseDaoTests{

    private JdbcUserDao sut;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcUserDao(jdbcTemplate);
    }

    @Test
    public void createNewUser() {
        boolean userCreated = sut.create("TEST_USER","test_password");
        Assert.assertTrue(userCreated);
        User user = sut.findByUsername("TEST_USER");
        Assert.assertEquals("TEST_USER", user.getUsername());
    }

    @Test
    public void return_bob_by_id_1001(){
        String expected = "bob";
        String result = sut.findUserNameById(1001);
        Assert.assertEquals(expected, result);
    }

    @Test
    public void return_user_by_id_1002(){
        String expected = "user";
        String result = sut.findUserNameById(1002);
        Assert.assertEquals(expected, result);
    }

    @Test
    public void return_true_for_JJ(){
        boolean expected = true;
        boolean actual = sut.create("JJ", "JJ");
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void return_false_for_AA(){
        boolean expected = true;
        boolean actual = sut.create("AA", "AA");
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void return_userList(){
        List<UserDTO> actual = new ArrayList<>();
        UserDTO user1 = new UserDTO(1001, "bob");
        actual.add(user1);
        UserDTO user2 = new UserDTO(1002, "user");
        actual.add(user2);
        List<UserDTO> expected = sut.findAllUserNames();
        Assert.assertEquals(expected.get(0).getUserName(), actual.get(0).getUserName());
    }
}
