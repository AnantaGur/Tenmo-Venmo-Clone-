package com.techelevator.tenmo.controller;

import javax.validation.Valid;

import com.techelevator.tenmo.dao.JdbcBalance;
import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.security.jwt.TokenProvider;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

/**
 * Controller to authenticate users.
 */

@RestController
@PreAuthorize("isAuthenticated()")
public class AuthenticationController {

    @Autowired
    JdbcBalance jdbcBalance;
    @Autowired
    JdbcUserDao jdbcUserDao;

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private UserDao userDao;

    public AuthenticationController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder, UserDao userDao) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userDao = userDao;
    }

    @PreAuthorize("permitAll")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public LoginResponse login(@Valid @RequestBody LoginDTO loginDto) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createToken(authentication, false);
        
        User user = userDao.findByUsername(loginDto.getUsername());

        return new LoginResponse(jwt, user);
    }


    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("permitAll")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public void register(@Valid @RequestBody RegisterUserDTO newUser) {
        if (!userDao.create(newUser.getUsername(), newUser.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User registration failed.");
        }
    }

    @RequestMapping(value = "/balance", method = RequestMethod.GET)
    public BigDecimal getBalance(Principal principal) {
        String userName = principal.getName();
        int userId = jdbcBalance.findByUsername(userName);
        BigDecimal balance = jdbcBalance.getBalance(userId);
        return balance;
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value = "/transfer")
    public String transfer(Principal principal, @RequestBody TransferDTO transferDTO) {
        UserDTO userDTO = new UserDTO();
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
    /**
     * Object to return as body in JWT Authentication.
     */
    static class LoginResponse {

        private String token;
        private User user;

        LoginResponse(String token, User user) {
            this.token = token;
            this.user = user;
        }

        public String getToken() {
            return token;
        }

        void setToken(String token) {
            this.token = token;
        }

		public User getUser() {
			return user;
		}

		public void setUser(User user) {
			this.user = user;
		}
    }
}

