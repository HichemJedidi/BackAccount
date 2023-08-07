package com.sgcib.bankacountapplication.query.service;

import com.sgcib.bankacountapplication.commons.enums.OperationType;
import com.sgcib.bankacountapplication.commons.events.AccountActivatedEvent;
import com.sgcib.bankacountapplication.commons.events.AccountCreatedEvent;
import com.sgcib.bankacountapplication.commons.events.AccountCreditedEvent;
import com.sgcib.bankacountapplication.commons.events.AccountDebitedEvent;
import com.sgcib.bankacountapplication.commons.queries.GetAccountsByIdQuery;
import com.sgcib.bankacountapplication.commons.queries.GetAllAccountsQuery;
import com.sgcib.bankacountapplication.query.entities.Account;
import com.sgcib.bankacountapplication.query.entities.Operation;
import com.sgcib.bankacountapplication.query.repositories.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
@Transactional
public class AccountServiceHandler {
    private AccountRepository accountRepository;
    private OperationRepository operationRepository;
    @EventHandler
    public  void on(AccountCreatedEvent event){
        log.info("**************************");
        log.info("l'evenement AccountCreatedEvent reçu");
        Account account = new Account();
        account.setId(event.getId());
        account.setCurrency(event.getCurrency());
        account.setStatus(event.getStatus());
        account.setBalance(event.getIntialBalance());
        accountRepository.save(account);

    }
    @EventHandler
    public  void on(AccountActivatedEvent event){
        log.info("**************************");
        log.info("l'evenement AccountActivatedEvent reçu");
        Account account=accountRepository.findById(event.getId()).get();
        account.setStatus(event.getStatus());
        accountRepository.save(account);

    }
    @EventHandler
    public  void on(AccountCreditedEvent event){
        log.info("**************************");
        log.info("l'evenement AccountCreditedEvent reçu");
        Account account=accountRepository.findById(event.getId()).get();
        Operation operation = new Operation();
        operation.setAmount(event.getAmount());
        operation.setDate(new Date());
        operation.setType(OperationType.WITHDRAW);
        operation.setAccount(account);
        operationRepository.save(operation);
        account.setBalance(account.getBalance()+event.getAmount());
        accountRepository.save(account);

    }
    @EventHandler
    public  void on(AccountDebitedEvent event){
        log.info("**************************");
        log.info("l'evenement AccountDebitedEvent reçu");
        Account account=accountRepository.findById(event.getId()).get();
        Operation operation = new Operation();
        operation.setAmount(event.getAmount());
        operation.setDate(new Date());
        operation.setType(OperationType.DEPOSIT);
        operation.setAccount(account);
        operationRepository.save(operation);
        account.setBalance(account.getBalance()-event.getAmount());
        accountRepository.save(account);

    }
    @QueryHandler
    public List<Account> on(GetAllAccountsQuery query){
        return accountRepository.findAll();
    }
    @QueryHandler
    public Account on (GetAccountsByIdQuery query){
        return accountRepository.findById(query.getId()).get();
    }
}
