package com.sgcib.bankacountapplication.commands.aggergates;

import com.sgcib.bankacountapplication.commons.exeptions.AmountNegativeExeption;
import com.sgcib.bankacountapplication.commons.commands.CreateAccountCommand;
import com.sgcib.bankacountapplication.commons.commands.DepositMoneyCommand;
import com.sgcib.bankacountapplication.commons.commands.WithdrawMoneyCommand;
import com.sgcib.bankacountapplication.commons.enums.AccountStatus;
import com.sgcib.bankacountapplication.commons.events.AccountActivatedEvent;
import com.sgcib.bankacountapplication.commons.events.AccountCreatedEvent;
import com.sgcib.bankacountapplication.commons.events.AccountCreditedEvent;
import com.sgcib.bankacountapplication.commons.events.AccountDebitedEvent;
import com.sgcib.bankacountapplication.commons.exeptions.BalanceNotSufficentExeption;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@Aggregate
public class AccountAggregate {
    @AggregateIdentifier
    private String accountId;
    private double balance;
    private String currency;
    private AccountStatus status;

    public AccountAggregate() {
        //Required by AXON
    }
    @CommandHandler
    public AccountAggregate(CreateAccountCommand createAccountCommand) {
        if (createAccountCommand.getInitialBalance()<0) throw new RuntimeException("Impossible de créer le compte il faut un montant supérieur ou egale a zero");
        //OK
        AggregateLifecycle.apply(new AccountCreatedEvent(
                createAccountCommand.getId(),
                createAccountCommand.getInitialBalance(),
                createAccountCommand.getCurrency(),
                AccountStatus.CREATED));
    }
    @EventSourcingHandler
    public void on(AccountCreatedEvent event){
        this.accountId=event.getId();
        this.balance= event.getIntialBalance();
        this.currency= event.getCurrency();
        this.status=AccountStatus.CREATED;
        AggregateLifecycle.apply(new AccountActivatedEvent(
                event.getId(),
                AccountStatus.ACTIVATED
        ));
    }
    @EventSourcingHandler
    public void on(AccountActivatedEvent event){
        this.status=event.getStatus();
    }
    @CommandHandler
    public void handle(WithdrawMoneyCommand command){
        if (command.getAmount()<0) throw new AmountNegativeExeption("le montant ne doit pas etre negative");
        AggregateLifecycle.apply(new AccountCreditedEvent(
                command.getId(),
                command.getAmount(),
                command.getCurrency()
        ));
    }
    @EventSourcingHandler
    public void on(AccountCreditedEvent event){
        this.balance+= event.getAmount();
    }
    @CommandHandler
    public void handle(DepositMoneyCommand command){
        if (command.getAmount()<0) throw new AmountNegativeExeption("le montant ne doit pas etre negative");
        if (this.balance<command.getAmount()) throw new BalanceNotSufficentExeption("le montant est inssufisant=>"+balance);
        AggregateLifecycle.apply(new AccountDebitedEvent(
                command.getId(),
                command.getAmount(),
                command.getCurrency()
        ));
    }
    @EventSourcingHandler
    public void on(AccountDebitedEvent event){
        this.balance-= event.getAmount();
    }
}
