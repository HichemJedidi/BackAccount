package com.sgcib.bankacountapplication.command;

import com.sgcib.bankacountapplication.commands.aggergates.AccountAggregate;
import com.sgcib.bankacountapplication.commons.commands.CreateAccountCommand;
import com.sgcib.bankacountapplication.commons.commands.DepositMoneyCommand;
import com.sgcib.bankacountapplication.commons.commands.WithdrawMoneyCommand;
import com.sgcib.bankacountapplication.commons.enums.AccountStatus;
import com.sgcib.bankacountapplication.commons.events.AccountActivatedEvent;
import com.sgcib.bankacountapplication.commons.events.AccountCreatedEvent;
import com.sgcib.bankacountapplication.commons.events.AccountCreditedEvent;
import com.sgcib.bankacountapplication.commons.events.AccountDebitedEvent;
import com.sgcib.bankacountapplication.commons.exeptions.AmountNegativeExeption;
import com.sgcib.bankacountapplication.commons.exeptions.BalanceNotSufficentExeption;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AccountAggregateTest {

    private AggregateTestFixture<AccountAggregate> fixture;

    @BeforeEach
    public void setUp() {
        fixture = new AggregateTestFixture<>(AccountAggregate.class);
    }

    @Test
    public void testCreateAccountCommand() {
        String accountId = "12345";
        double initialBalance = 1000;
        String currency = "EUR";

        fixture.givenNoPriorActivity()
                .when(new CreateAccountCommand(accountId, initialBalance, currency))
                .expectEvents(new AccountCreatedEvent(accountId, initialBalance, currency, AccountStatus.CREATED),
                        new AccountActivatedEvent(accountId, AccountStatus.ACTIVATED));
    }

    @Test
    public void testDepositMoneyCommand() {
        String accountId = "12345";
        double initialBalance = 1000;
        String currency = "EUR";
        double depositAmount = 500;

        fixture.given(new AccountCreatedEvent(accountId, initialBalance, currency, AccountStatus.CREATED),
                new AccountActivatedEvent(accountId, AccountStatus.ACTIVATED))
                .when(new DepositMoneyCommand(accountId, depositAmount, currency))
                .expectEvents(new AccountDebitedEvent(accountId, depositAmount, currency));
    }

    @Test
    public void testWithdrawMoneyCommand() {
        String accountId = "12345";
        double initialBalance = 1000;
        String currency = "EUR";
        double withdrawAmount = 500;

        fixture.given(new AccountCreatedEvent(accountId, initialBalance, currency, AccountStatus.CREATED),
                new AccountActivatedEvent(accountId, AccountStatus.ACTIVATED))
                .when(new WithdrawMoneyCommand(accountId, withdrawAmount, currency))
                .expectEvents(new AccountCreditedEvent(accountId, withdrawAmount, currency));
    }

    @Test
    public void testDepositMoneyCommandWithInsufficientBalance() {
        String accountId = "12345";
        double initialBalance = 1000;
        String currency = "EUR";
        double depositAmount = 1500;

        fixture.given(new AccountCreatedEvent(accountId, initialBalance, currency, AccountStatus.CREATED),
                new AccountActivatedEvent(accountId, AccountStatus.ACTIVATED))
                .when(new DepositMoneyCommand(accountId, depositAmount, currency))
                .expectException(BalanceNotSufficentExeption.class);
    }

    @Test
    public void testNegativeAmountDepositCommand() {
        String accountId = "12345";
        double initialBalance = 1000;
        String currency = "EUR";
        double depositAmount = -500;

        fixture.given(new AccountCreatedEvent(accountId, initialBalance, currency, AccountStatus.CREATED),
                new AccountActivatedEvent(accountId, AccountStatus.ACTIVATED))
                .when(new DepositMoneyCommand(accountId, depositAmount, currency))
                .expectException(AmountNegativeExeption.class);
    }

    @Test
    public void testNegativeAmountWithdrawCommand() {
        String accountId = "12345";
        double initialBalance = 1000;
        String currency = "EUR";
        double withdrawAmount = -500;

        fixture.given(new AccountCreatedEvent(accountId, initialBalance, currency, AccountStatus.CREATED),
                new AccountActivatedEvent(accountId, AccountStatus.ACTIVATED))
                .when(new WithdrawMoneyCommand(accountId, withdrawAmount, currency))
                .expectException(AmountNegativeExeption.class);
    }
}