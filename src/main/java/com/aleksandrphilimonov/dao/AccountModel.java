package com.aleksandrphilimonov.dao;

import java.math.BigDecimal;

public class AccountModel {
    private long accountId;
    private String title;
    private BigDecimal balance;
    private long userId;

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "AccountModel{" +
                "accountId=" + accountId +
                ", title='" + title + '\'' +
                ", balance=" + balance +
                ", userId=" + userId +
                '}';
    }
}
