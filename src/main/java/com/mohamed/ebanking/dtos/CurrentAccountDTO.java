package com.mohamed.ebanking.dtos;

import com.mohamed.ebanking.enums.AccountStatus;
import lombok.Data;

import java.util.Date;

@Data
public class CurrentAccountDTO extends BankAccountDTO {
    private String id;
    private double balance;
    private Date createdAt;
    private AccountStatus status;
    private CustomerDTO customerDTO;
    private double overDraft;
}
