package com.banking.account_service.controller;

import com.banking.account_service.model.Account;
import com.banking.account_service.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping
    public List<Account> getAllAccounts() {
        return accountService.getAllAccounts();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id) {
        return accountService.getAccountById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Account createAccount(@RequestBody Account account) {
        return accountService.saveAccount(account);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{accountNumber}")
    public ResponseEntity<Account> updateAccount(@PathVariable String accountNumber, @RequestBody Account account) {
        Optional<Account> existingAccount = accountService.getAccountById(account.getId());

        if (existingAccount.isPresent()) {
            Account updatedAccount = accountService.updateAccount(account);
            return ResponseEntity.ok(updatedAccount);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<Account> getAccountByNumber(@PathVariable String accountNumber) {
        return accountService.getAccountByAccountNumber(accountNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}