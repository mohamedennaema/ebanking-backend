package com.mohamed.ebanking.web;

import com.mohamed.ebanking.dtos.*;
import com.mohamed.ebanking.exceptions.BalanceNotSufficientException;
import com.mohamed.ebanking.exceptions.BankAcountNotFoundException;
import com.mohamed.ebanking.services.BankAccountService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
public class BankAccountRestController {
    private BankAccountService bankAccountService;

    @GetMapping("/accounts")
    public List<BankAccountDTO> getBankAcounts() {
        return bankAccountService.bankAccountList();
    }

    @GetMapping("/customer-accounts/{id}")
    public List<BankAccountDTO> getCustomerAcounts(@PathVariable Long id) {
        return bankAccountService.getCustomerAcounts(id);
    }

    @GetMapping("/accounts/{id}")
    public BankAccountDTO getBankAccount(@PathVariable String id) throws BankAcountNotFoundException {
        return bankAccountService.getBankAccount(id);
    }

    @GetMapping("/accounts/{id}/operations")
    public List<AccountOperationDTO> getAccountOperations(@PathVariable String id) {
        return bankAccountService.getAccountOperations(id);
    }

    @GetMapping("/accounts/{id}/operationsPage")
    public AccountHistoryDTO getAccountOperationsPage(
            @PathVariable String id,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size) throws BankAcountNotFoundException {
        return bankAccountService.getAccountOperationsPage(id, page, size);
    }

    @PostMapping("/accounts/debit")
    public DebitDTO debit(@RequestBody DebitDTO debitDTO)
            throws BalanceNotSufficientException, BankAcountNotFoundException {
        this.bankAccountService.debit(debitDTO.getAccountID(), debitDTO.getAmount(), debitDTO.getDescription());
        return debitDTO;
    }

    @PostMapping("/accounts/credit")
    public CreditDTO credit(@RequestBody CreditDTO creditDTO) throws BankAcountNotFoundException {
        this.bankAccountService.credit(creditDTO.getAccountID(), creditDTO.getAmount(), creditDTO.getDescription());
        return creditDTO;
    }

    @PostMapping("/accounts/transfer")
    public void credit(@RequestBody TransferRequestDTO transferRequestDTO)
            throws BalanceNotSufficientException, BankAcountNotFoundException {
        this.bankAccountService.transfer(
                transferRequestDTO.getAccountSource(),
                transferRequestDTO.getAccountDestination(),
                transferRequestDTO.getAmount(),
                transferRequestDTO.getDescription());
    }
}
