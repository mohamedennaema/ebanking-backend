package com.mohamed.ebanking.services;

import com.mohamed.ebanking.dtos.*;
import com.mohamed.ebanking.entities.BankAccount;
import com.mohamed.ebanking.entities.CurrentAccount;
import com.mohamed.ebanking.entities.Customer;
import com.mohamed.ebanking.entities.SavingAccount;
import com.mohamed.ebanking.exceptions.BalanceNotSufficientException;
import com.mohamed.ebanking.exceptions.BankAcountNotFoundException;
import com.mohamed.ebanking.exceptions.CustomerNotFoundException;

import java.util.List;

public interface BankAccountService {
    CustomerDTO saveCustomer(CustomerDTO customerDTO);

    CurrentAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId)
            throws CustomerNotFoundException;

    SavingAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId)
            throws CustomerNotFoundException;

    List<CustomerDTO> listCustomer();

    BankAccountDTO getBankAccount(String accountId) throws BankAcountNotFoundException;

    void debit(String accountId, double amount, String description)
            throws BankAcountNotFoundException, BalanceNotSufficientException;

    void credit(String accountId, double amount, String description) throws BankAcountNotFoundException;

    void transfer(String accountIdSource, String accountIdDestination, double amount, String description)
            throws BalanceNotSufficientException, BankAcountNotFoundException;

    List<BankAccountDTO> bankAccountList();

    CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException;

    CustomerDTO updateCustomer(CustomerDTO customerDTO);

    void deleteCustomer(Long customerId);

    List<AccountOperationDTO> getAccountOperations(String accountId);

    AccountHistoryDTO getAccountOperationsPage(String id, int page, int size) throws BankAcountNotFoundException;

    List<CustomerDTO> searchCustomers(String keyword);

    CustomerPageDTO getCustomersByPage(String keyword, int page, int size);

    List<BankAccountDTO> getCustomerAcounts(Long id);
}