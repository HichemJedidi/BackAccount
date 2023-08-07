package com.sgcib.bankacountapplication.commons.commands;

import lombok.Getter;

public class WithdrawMoneyCommand extends BaseCommand<String> {
    @Getter private double amount;
    @Getter private String currency;
    public WithdrawMoneyCommand(String id, double amount, String currency) {
        super(id);
        this.amount = amount;
        this.currency = currency;
    }
}
