package com.sgcib.bankacountapplication.commons.dtos;

import lombok.Data;

@Data
public class DepositMoneyRequestDTO {
    private String accountId;
    private double amount;
    private String currency;
}
