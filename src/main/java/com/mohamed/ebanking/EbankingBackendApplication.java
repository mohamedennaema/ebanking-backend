package com.mohamed.ebanking;

import com.mohamed.ebanking.dtos.CurrentAccountDTO;
import com.mohamed.ebanking.dtos.CustomerDTO;
import com.mohamed.ebanking.dtos.SavingAccountDTO;
import com.mohamed.ebanking.entities.AccountOperation;
import com.mohamed.ebanking.entities.CurrentAccount;
import com.mohamed.ebanking.entities.Customer;
import com.mohamed.ebanking.entities.SavingAccount;
import com.mohamed.ebanking.enums.AccountStatus;
import com.mohamed.ebanking.enums.OperationType;
import com.mohamed.ebanking.exceptions.BalanceNotSufficientException;
import com.mohamed.ebanking.exceptions.BankAcountNotFoundException;
import com.mohamed.ebanking.exceptions.CustomerNotFoundException;
import com.mohamed.ebanking.repositoreis.AccounctOperationRepository;
import com.mohamed.ebanking.repositoreis.BankAccountRepository;
import com.mohamed.ebanking.repositoreis.CustomerRepository;
import com.mohamed.ebanking.services.BankAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class EbankingBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EbankingBackendApplication.class, args);
    }

    // @Bean
    CommandLineRunner commandLineRunner(BankAccountService bankAccountService) {
        return args -> {
            Stream.of("mohamed", "Ayoub", "Salma").forEach(name -> {
                CustomerDTO customer = new CustomerDTO();
                customer.setName(name);
                customer.setEmail(name + "@gmail.com");
                bankAccountService.saveCustomer(customer);
            });

            bankAccountService.listCustomer().forEach(customer -> {
                try {
                    bankAccountService.saveCurrentBankAccount(Math.random() * 90000, 90000, customer.getId());
                    bankAccountService.saveSavingBankAccount(Math.random() * 120000, 5.5, customer.getId());
                } catch (CustomerNotFoundException e) {
                    e.printStackTrace();
                }
            });

            bankAccountService.bankAccountList().forEach(bankAccountDTO -> {
                for (int i = 0; i < 10; i++) {
                    String accountId;
                    if (bankAccountDTO instanceof CurrentAccountDTO)
                        accountId = ((CurrentAccountDTO) bankAccountDTO).getId();
                    else
                        accountId = ((SavingAccountDTO) bankAccountDTO).getId();
                    try {
                        bankAccountService.credit(accountId, 10000 + Math.random() * 120000, "Credit");
                        bankAccountService.debit(accountId, 1000 + Math.random() * 9000, "Debit");
                    } catch (BankAcountNotFoundException | BalanceNotSufficientException e) {
                        e.printStackTrace();
                    }
                }
            });
        };
    }

    // @Bean
    CommandLineRunner start(
            CustomerRepository customerRepository,
            BankAccountRepository bankAccountRepository,
            AccounctOperationRepository accounctOperationRepository) {
        return args -> {
            Stream.of("mohamed", "Ayoub", "Salma").forEach(name -> {
                Customer customer = new Customer();
                customer.setName(name);
                customer.setEmail(name + "@gmail.com");
                customerRepository.save(customer);
            });

            customerRepository.findAll().forEach(customer -> {
                CurrentAccount currentAccount = new CurrentAccount();
                currentAccount.setId(UUID.randomUUID().toString());
                currentAccount.setBalance(Math.random() * 90000);
                currentAccount.setStatus(AccountStatus.CREATED);
                currentAccount.setCreatedAt(new Date());
                currentAccount.setCustomer(customer);
                currentAccount.setOverDraft(90000);
                bankAccountRepository.save(currentAccount);

                SavingAccount savingAccount = new SavingAccount();
                savingAccount.setId(UUID.randomUUID().toString());
                savingAccount.setBalance(Math.random() * 90000);
                savingAccount.setStatus(AccountStatus.CREATED);
                savingAccount.setCreatedAt(new Date());
                savingAccount.setCustomer(customer);
                savingAccount.setInterestRate(5.5);
                bankAccountRepository.save(savingAccount);
            });

            bankAccountRepository.findAll().forEach(bankAccount -> {
                for (int i = 0; i < 10; i++) {
                    AccountOperation accountOperation = new AccountOperation();
                    accountOperation.setOperationDate(new Date());
                    accountOperation.setAmount(Math.random() * 12000);
                    accountOperation.setType(Math.random() > 0.5 ? OperationType.DEBIT : OperationType.CREDIT);
                    accountOperation.setBankAccount(bankAccount);
                    accounctOperationRepository.save(accountOperation);
                }
            });
        };
    }
}
