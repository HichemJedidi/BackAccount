package com.sgcib.bankacountapplication.commons.events;

import com.sgcib.bankacountapplication.commons.enums.AccountStatus;
import lombok.Getter;

public class AccountActivatedEvent extends BaseEvent<String>{
    @Getter private AccountStatus Status;

    public AccountActivatedEvent(String id ,AccountStatus status) {
        super(id);
        Status = status;
    }
}
