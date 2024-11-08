package ru.expensive.implement.screens.title.account;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

//Потом если сделаешь свою реализацию тут просто заглушка

@Getter
public class AccountManager {
    private final List<Account> accounts = new ArrayList<>();

    public void add(Account account) {
        accounts.add(account);
    }

    public void delete(Account account) {
        accounts.remove(account);
    }
}
