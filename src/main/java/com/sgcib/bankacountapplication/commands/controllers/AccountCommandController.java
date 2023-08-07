package com.sgcib.bankacountapplication.commands.controllers;


import com.sgcib.bankacountapplication.commons.commands.CreateAccountCommand;
import com.sgcib.bankacountapplication.commons.commands.DepositMoneyCommand;
import com.sgcib.bankacountapplication.commons.commands.WithdrawMoneyCommand;
import com.sgcib.bankacountapplication.commons.dtos.CreateAccountRequestDTO;
import com.sgcib.bankacountapplication.commons.dtos.DepositMoneyRequestDTO;
import com.sgcib.bankacountapplication.commons.dtos.WithdrawMoneyRequestDTO;
import lombok.AllArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;

import org.axonframework.eventsourcing.eventstore.EventStore;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

@RestController
@RequestMapping(path = "/commands/account")
@AllArgsConstructor


public class AccountCommandController {
    private CommandGateway commandeGateway;
    private EventStore eventStore;

    public AccountCommandController() {

    }

    @PostMapping(path = "/create")
    public CompletableFuture<String> createAccount(@RequestBody CreateAccountRequestDTO request){
        CompletableFuture<String> commandResponse =commandeGateway.send(new CreateAccountCommand(
                UUID.randomUUID().toString(),
                request.getInitialBalance(),
                request.getCurrency()
        ));
        return commandResponse;

    }

    @PutMapping(path = "/withdraw")
    public CompletableFuture<String> withdrawMoney(@RequestBody WithdrawMoneyRequestDTO request){
        CompletableFuture<String> commandResponse =commandeGateway.send(new WithdrawMoneyCommand(
                request.getAccountId(),
                request.getAmount(),
                request.getCurrency()
        ));
        return commandResponse;

    }
    @PutMapping(path = "/deposit")
    public CompletableFuture<String> depositMoney(@RequestBody DepositMoneyRequestDTO request){
        CompletableFuture<String> commandResponse =commandeGateway.send(new DepositMoneyCommand(
                request.getAccountId(),
                request.getAmount(),
                request.getCurrency()
        ));
        return commandResponse;

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> exeptionHandler(Exception exeption){
        ResponseEntity<String> entity = new ResponseEntity<>(
                exeption.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
        return entity;
    }
    @GetMapping("/eventStore/{accountId}")
    public Stream eventStore(@PathVariable String accountId){
        return eventStore.readEvents(accountId).asStream();
    }
}
