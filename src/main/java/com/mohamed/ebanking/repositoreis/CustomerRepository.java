package com.mohamed.ebanking.repositoreis;

import com.mohamed.ebanking.entities.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findCustomersByNameContains(String keyword);

    Page<Customer> findCustomersByNameContains(String keyword, Pageable pageable);
}