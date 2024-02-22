package com.mohamed.ebanking.services;

import com.mohamed.ebanking.dtos.*;
import com.mohamed.ebanking.entities.*;
import com.mohamed.ebanking.enums.AccountStatus;
import com.mohamed.ebanking.enums.OperationType;
import com.mohamed.ebanking.exceptions.BalanceNotSufficientException;
import com.mohamed.ebanking.exceptions.BankAcountNotFoundException;
import com.mohamed.ebanking.exceptions.CustomerNotFoundException;
import com.mohamed.ebanking.mappers.BankAccountMapperImpl;
import com.mohamed.ebanking.repositoreis.AccounctOperationRepository;
import com.mohamed.ebanking.repositoreis.BankAccountRepository;
import com.mohamed.ebanking.repositoreis.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
@Slf4j // Logger log= LoggerFactory.getLogger(this.getClass().getName());
public class BankAccountServiceImpl implements BankAccountService {
    private CustomerRepository customerRepository;
    private BankAccountRepository bankAccountRepository;
    private AccounctOperationRepository accounctOperationRepository;
    private BankAccountMapperImpl dtoMapper;

    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        log.info("Saving new Customer");
        Customer customer = dtoMapper.fromCustomerDTO(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return dtoMapper.fromCustomer(savedCustomer);
    }

    @Override
    public CurrentAccountDTO saveCurrentBankAccount(double initialBalance, double overDraft, Long customerId)
            throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null)
            throw new CustomerNotFoundException("Customer not found");
        CurrentAccount currentAccount = new CurrentAccount();
        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setCreatedAt(new Date());
        currentAccount.setStatus(AccountStatus.CREATED);
        currentAccount.setBalance(initialBalance);
        currentAccount.setCustomer(customer);
        currentAccount.setOverDraft(overDraft);
        CurrentAccount savedCurrentAccount = bankAccountRepository.save(currentAccount);

        return dtoMapper.fromCurrentAccount(savedCurrentAccount);
    }

    @Override
    public SavingAccountDTO saveSavingBankAccount(double initialBalance, double interestRate, Long customerId)
            throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null)
            throw new CustomerNotFoundException("Customer not found");
        SavingAccount savingAccount = new SavingAccount();
        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setCreatedAt(new Date());
        savingAccount.setStatus(AccountStatus.CREATED);
        savingAccount.setBalance(initialBalance);
        savingAccount.setCustomer(customer);
        savingAccount.setInterestRate(interestRate);
        SavingAccount savedSavingAccount = bankAccountRepository.save(savingAccount);

        return dtoMapper.fromSavingAccount(savedSavingAccount);
    }

    @Override
    public List<CustomerDTO> listCustomer() {
        List<Customer> customers = customerRepository.findAll();
        List<CustomerDTO> customerDTOS = customers.stream()
                .map(customer -> dtoMapper.fromCustomer(customer))
                .collect(Collectors.toList());
        return customerDTOS;
    }

    @Override
    public BankAccountDTO getBankAccount(String accountId) throws BankAcountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAcountNotFoundException("BankAcount not found"));
        if (bankAccount instanceof SavingAccount) {
            SavingAccount savingAccount = (SavingAccount) bankAccount;
            return dtoMapper.fromSavingAccount(savingAccount);
        } else {
            CurrentAccount currentAccount = (CurrentAccount) bankAccount;
            return dtoMapper.fromCurrentAccount(currentAccount);
        }
    }

    @Override
    public void debit(String accountId, double amount, String description)
            throws BankAcountNotFoundException, BalanceNotSufficientException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAcountNotFoundException("BankAcount not found"));
        if (bankAccount.getBalance() < amount)
            throw new BalanceNotSufficientException("Balance not sufficient");
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.DEBIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accounctOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance() - amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void credit(String accountId, double amount, String description) throws BankAcountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(() -> new BankAcountNotFoundException("BankAcount not found"));
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setType(OperationType.CREDIT);
        accountOperation.setAmount(amount);
        accountOperation.setDescription(description);
        accountOperation.setOperationDate(new Date());
        accountOperation.setBankAccount(bankAccount);
        accounctOperationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance() + amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount, String description)
            throws BalanceNotSufficientException, BankAcountNotFoundException {
        debit(accountIdSource, amount, description);
        credit(accountIdDestination, amount, description);
    }

    @Override
    public List<BankAccountDTO> bankAccountList() {
        List<BankAccount> bankAccounts = bankAccountRepository.findAll();
        List<BankAccountDTO> bankAccountDTOS = bankAccounts.stream()
                .map(bankAccount -> {
                    if (bankAccount instanceof SavingAccount) {
                        SavingAccount savingAccount = (SavingAccount) bankAccount;
                        return dtoMapper.fromSavingAccount(savingAccount);
                    } else {
                        CurrentAccount currentAccount = (CurrentAccount) bankAccount;
                        return dtoMapper.fromCurrentAccount(currentAccount);
                    }
                }).collect(Collectors.toList());
        return bankAccountDTOS;
    }

    @Override
    public CustomerDTO getCustomer(Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found"));
        return dtoMapper.fromCustomer(customer);
    }

    @Override
    public CustomerDTO updateCustomer(CustomerDTO customerDTO) {
        log.info("Saving new Customer");
        Customer customer = dtoMapper.fromCustomerDTO(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return dtoMapper.fromCustomer(savedCustomer);
    }

    @Override
    public void deleteCustomer(Long customerId) {
        customerRepository.deleteById(customerId);
    }

    @Override
    public List<AccountOperationDTO> getAccountOperations(String accountId) {
        List<AccountOperation> accountOperations = accounctOperationRepository
                .findAccountOperationByBankAccountId(accountId);
        List<AccountOperationDTO> accountOperationDTOS = accountOperations.stream()
                .map(accountOperation -> dtoMapper.fromAccountOperatoin(accountOperation))
                .collect(Collectors.toList());
        return accountOperationDTOS;
    }

    @Override
    public AccountHistoryDTO getAccountOperationsPage(String id, int page, int size)
            throws BankAcountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(id)
                .orElseThrow(() -> new BankAcountNotFoundException("BankAcount not found"));
        Page<AccountOperation> accountOperations = accounctOperationRepository
                .findAccountOperationByBankAccountIdOrderByOperationDateDesc(id, PageRequest.of(page, size));
        List<AccountOperationDTO> accountOperationDTOS = accountOperations.stream()
                .map(accountOperation -> dtoMapper.fromAccountOperatoin(accountOperation))
                .collect(Collectors.toList());
        AccountHistoryDTO accountHistoryDTO = new AccountHistoryDTO();
        accountHistoryDTO.setAccountId(id);
        accountHistoryDTO.setBalance(bankAccount.getBalance());
        accountHistoryDTO.setCurrentPage(page);
        accountHistoryDTO.setTotalPages(accountOperations.getTotalPages());
        accountHistoryDTO.setPageSize(size);
        accountHistoryDTO.setAccountOperationDTOS(accountOperationDTOS);
        return accountHistoryDTO;
    }

    @Override
    public List<CustomerDTO> searchCustomers(String keyword) {
        List<Customer> customers = customerRepository.findCustomersByNameContains(keyword);
        List<CustomerDTO> customerDTOS = customers.stream()
                .map(customer -> dtoMapper.fromCustomer(customer))
                .collect(Collectors.toList());
        return customerDTOS;
    }

    @Override
    public CustomerPageDTO getCustomersByPage(String keyword, int page, int size) {
        Page<Customer> customers = customerRepository.findCustomersByNameContains(keyword, PageRequest.of(page, size));
        List<CustomerDTO> customerDTOS = customers.stream()
                .map(customer -> dtoMapper.fromCustomer(customer))
                .collect(Collectors.toList());
        CustomerPageDTO customerPageDTO = new CustomerPageDTO();
        customerPageDTO.setCurrentPage(page);
        customerPageDTO.setPageSize(size);
        customerPageDTO.setTotalPages(customers.getTotalPages());
        customerPageDTO.setCustomerDTOS(customerDTOS);
        return customerPageDTO;
    }

    @Override
    public List<BankAccountDTO> getCustomerAcounts(Long id) {
        List<BankAccount> bankAccounts = bankAccountRepository.findByCustomer_Id(id);
        List<BankAccountDTO> bankAccountDTOS = bankAccounts.stream()
                .map(bankAccount -> {
                    if (bankAccount instanceof SavingAccount) {
                        SavingAccount savingAccount = (SavingAccount) bankAccount;
                        return dtoMapper.fromSavingAccount(savingAccount);
                    } else {
                        CurrentAccount currentAccount = (CurrentAccount) bankAccount;
                        return dtoMapper.fromCurrentAccount(currentAccount);
                    }
                }).collect(Collectors.toList());
        return bankAccountDTOS;
    }
}