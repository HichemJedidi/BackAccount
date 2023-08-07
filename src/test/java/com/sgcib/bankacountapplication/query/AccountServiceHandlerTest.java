package com.sgcib.bankacountapplication.query;

import com.sgcib.bankacountapplication.commons.enums.AccountStatus;
import com.sgcib.bankacountapplication.commons.events.AccountActivatedEvent;
import com.sgcib.bankacountapplication.commons.events.AccountCreatedEvent;
import com.sgcib.bankacountapplication.commons.events.AccountCreditedEvent;
import com.sgcib.bankacountapplication.commons.events.AccountDebitedEvent;
import com.sgcib.bankacountapplication.commons.queries.GetAccountsByIdQuery;
import com.sgcib.bankacountapplication.commons.queries.GetAllAccountsQuery;
import com.sgcib.bankacountapplication.query.entities.Account;
import com.sgcib.bankacountapplication.query.repositories.*;
import com.sgcib.bankacountapplication.query.service.AccountServiceHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AccountServiceHandlerTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private OperationRepository operationRepository;

    @InjectMocks
    private AccountServiceHandler accountServiceHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void onAccountCreatedEvent_shouldSaveAccount() {
        AccountCreatedEvent event = new AccountCreatedEvent("account-id", 1000,"USD", AccountStatus.CREATED);

        accountServiceHandler.on(event);

        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    void onAccountActivatedEvent_shouldUpdateAccountStatus() {
        Account account = new Account();
        account.setId("account-id");
        account.setStatus(AccountStatus.CREATED);

        when(accountRepository.findById("account-id")).thenReturn(Optional.of(account));

        AccountActivatedEvent event = new AccountActivatedEvent("account-id", AccountStatus.ACTIVATED);

        accountServiceHandler.on(event);

        assertEquals(AccountStatus.ACTIVATED, account.getStatus());
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void onAccountCreditedEvent_shouldUpdateAccountBalanceAndSaveOperation() {
        Account account = new Account();
        account.setId("account-id");
        account.setStatus(AccountStatus.ACTIVATED);
        account.setBalance(100.0);

        when(accountRepository.findById("account-id")).thenReturn(Optional.of(account));

        AccountCreditedEvent event = new AccountCreditedEvent("account-id", 50.0,"TND");

        accountServiceHandler.on(event);

        assertEquals(150.0, account.getBalance());
        verify(accountRepository, times(1)).save(account);

        verify(operationRepository, times(1)).save(any());
    }

    @Test
    void onAccountDebitedEvent_shouldUpdateAccountBalanceAndSaveOperation() {
        Account account = new Account();
        account.setId("account-id");
        account.setStatus(AccountStatus.ACTIVATED);
        account.setBalance(100.0);

        when(accountRepository.findById("account-id")).thenReturn(Optional.of(account));

        AccountDebitedEvent event = new AccountDebitedEvent("account-id", 50.0,"EUR");

        accountServiceHandler.on(event);

        assertEquals(50.0, account.getBalance());
        verify(accountRepository, times(1)).save(account);

        verify(operationRepository, times(1)).save(any());
    }

    @Test
    void onGetAllAccountsQuery_shouldReturnAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        accounts.add(new Account("account-id-1",1000, AccountStatus.ACTIVATED,"TND" , null));
        accounts.add(new Account("account-id-2", 100, AccountStatus.ACTIVATED,"TND", null));

        when(accountRepository.findAll()).thenReturn(accounts);

        GetAllAccountsQuery query = new GetAllAccountsQuery();

        List<Account> result = accountServiceHandler.on(query);

        assertEquals(accounts.size(), result.size());
    }

    @Test
    void onGetAccountsByIdQuery_shouldReturnAccountById() {
        Account account = new Account("account-id-1",1000, AccountStatus.ACTIVATED,"TND" , null);

        when(accountRepository.findById("account-id")).thenReturn(Optional.of(account));

        GetAccountsByIdQuery query = new GetAccountsByIdQuery("account-id");

        Account result = accountServiceHandler.on(query);

        assertEquals(account.getId(), result.getId());
    }
}
