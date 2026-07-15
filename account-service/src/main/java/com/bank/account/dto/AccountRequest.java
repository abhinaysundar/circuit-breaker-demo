package com.bank.account.dto;

import java.math.BigDecimal;

public class AccountRequest {
    private String accountHolder;
    private BigDecimal initialDeposit;

    public String getAccountHolder() { return accountHolder; }
    public void setAccountHolder(String accountHolder) { this.accountHolder = accountHolder; }

    public BigDecimal getInitialDeposit() { return initialDeposit; }
    public void setInitialDeposit(BigDecimal initialDeposit) { this.initialDeposit = initialDeposit; }
}
