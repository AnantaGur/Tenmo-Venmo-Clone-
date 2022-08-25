package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.DetailDTO;
import com.techelevator.tenmo.model.TransferDTO;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserDTO;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import javax.xml.crypto.Data;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;


@Component
public class JdbcBalance implements BalanceDao {
    JdbcTemplate jdbcTemplate;
    JdbcUserDao userDao;

    public JdbcBalance(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int findByUsername(String username) {
        String sql = "SELECT user_id, username FROM tenmo_user WHERE username ILIKE ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, username);
        if (rowSet.next()){
            return rowSet.getInt("user_id");
        }
        throw new UsernameNotFoundException("User " + username + " was not found.");
    }

    @Override
    public BigDecimal getBalance(int userId) throws UsernameNotFoundException {
        BigDecimal balance = new BigDecimal("-1");
        String sql = "SELECT balance FROM account WHERE user_id = ?;";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, userId);
        if (rowSet.next()){
            balance = rowSet.getBigDecimal("balance");
        }
        return balance;
    }

    @Override
    public String transfer(BigDecimal amount, int fromId, int toId) {
        BigDecimal fromBalance = getBalance(fromId);
        BigDecimal toBalance = getBalance(toId);
        if (getBalance(fromId).compareTo(BigDecimal.ZERO) > 0 &&
        getBalance(fromId).compareTo(amount) > 0 && fromId != toId) {
            fromBalance = fromBalance.subtract(amount);
            toBalance = getBalance(toId).add(amount);
        }
        String sql = "UPDATE account SET balance = ? WHERE user_id = ?";
        String sql2 = "UPDATE account SET balance = ? WHERE user_id = ? ";
        String sql3 = "INSERT INTO transfer (user_id_from, user_id_to, amount, status, type) " +
                "VALUES (?, ?, ?, 'APPROVED', 'SEND')";
        try{
            jdbcTemplate.update(sql, fromBalance, fromId);
            jdbcTemplate.update(sql2, toBalance, toId);
            jdbcTemplate.update(sql3, fromId, toId, amount);
        } catch (DataAccessException e){
            return "Rejected";
        }

        return "Approved";
    }

    public List<TransferDTO> getTransfersById(int userId){
        List<TransferDTO> transferDTOList = new ArrayList<>();
        String sql = "SELECT user_id_from, user_id_to, amount, status, type FROM  transfer " +
                "WHERE user_id_from = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, userId);
        while (rowSet.next()){
            transferDTOList.add(mapRowToTransferDTO(rowSet));
        }
        return transferDTOList;
    }

    public DetailDTO getTransferById(int transferId, String user){
        DetailDTO detailDTO = new DetailDTO();
        String sql = "SELECT transfer_id, username, amount, status, type " +
                "FROM transfer " +
                "JOIN tenmo_user ON transfer.user_id_to = tenmo_user.user_id " +
                "WHERE transfer_id = ?";
        SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, transferId);
        if (rowSet.next()){
            detailDTO = mapRowToTransferDTODetails(rowSet, user);
        }

/*        detailDTO = mapRowToTransferDTODetails(transferId, userDao.findUserNameById(transferDTO.getFromId()),
                userDao.findUserNameById(transferDTO.getToId()), transferDTO.getType(),
                transferDTO.getStatus(), transferDTO.getAmount());*/

        return detailDTO;
    }

    private TransferDTO mapRowToTransferDTO(SqlRowSet rs) {
        TransferDTO transferDTO = new TransferDTO();
        transferDTO.setFromId(rs.getInt("user_id_from"));
        transferDTO.setToId(rs.getInt("user_id_to"));
        transferDTO.setAmount(rs.getBigDecimal("amount"));
        transferDTO.setStatus(rs.getString("status"));
        transferDTO.setType(rs.getString("type"));
        return transferDTO;
    }

    private DetailDTO mapRowToTransferDTODetails(SqlRowSet rs, String user){
        DetailDTO detailDTO = new DetailDTO();
        detailDTO.setTransferId(rs.getInt("transfer_id"));
        detailDTO.setSender(user);
        detailDTO.setReceiver(rs.getString("username"));
        detailDTO.setType(rs.getString("type"));
        detailDTO.setStatus(rs.getString("status"));
        detailDTO.setAmount(rs.getBigDecimal("amount"));
        return detailDTO;
    }
}
