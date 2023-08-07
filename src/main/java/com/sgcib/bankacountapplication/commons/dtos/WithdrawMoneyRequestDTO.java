package com.sgcib.bankacountapplication.commons.dtos;

import lombok.Data;

@Data
public class WithdrawMoneyRequestDTO {
    private String accountId;
    private double amount;
    private String currency;
}
