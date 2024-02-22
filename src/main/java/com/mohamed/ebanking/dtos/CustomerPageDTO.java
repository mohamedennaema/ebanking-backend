package com.mohamed.ebanking.dtos;

import lombok.Data;

import java.util.List;

@Data
public class CustomerPageDTO {
    private int currentPage;
    private int totalPages;
    private int pageSize;
    private List<CustomerDTO> customerDTOS;
}
