package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class TransferDTO {

    private BigDecimal amount;
    private int fromId;
    private int toId;
    private String status;
    private String type;

    public TransferDTO(BigDecimal amount, int fromId, int toId, String status, String type) {
        this.amount = amount;
        this.fromId = fromId;
        this.toId = toId;
        this.status = status;
        this.type = type;
    }

    public TransferDTO(){

    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int getFromId() {
        return fromId;
    }

    public void setFromId(int fromId) {
        this.fromId = fromId;
    }

    public int getToId() {
        return toId;
    }

    public void setToId(int toId) {
        this.toId = toId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "TransferDTO{" +
                "amount=" + amount +
                ", fromId=" + fromId +
                ", toId=" + toId +
                ", status='" + status + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
