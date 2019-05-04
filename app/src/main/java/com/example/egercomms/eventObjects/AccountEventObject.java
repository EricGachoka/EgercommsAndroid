package com.example.egercomms.eventObjects;

import com.example.egercomms.models.Account;
import com.example.egercomms.models.Jurisdiction;

import java.util.ArrayList;
import java.util.List;

public class AccountEventObject {
    private List<Account> accounts;

    public AccountEventObject(List<Account> accounts) {
        this.accounts = accounts;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }
}
