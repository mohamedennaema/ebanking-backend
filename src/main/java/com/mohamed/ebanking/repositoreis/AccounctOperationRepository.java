package com.mohamed.ebanking.repositoreis;

import com.mohamed.ebanking.entities.AccountOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AccounctOperationRepository extends JpaRepository<AccountOperation, Long> {
    List<AccountOperation> findAccountOperationByBankAccountId(String accountId);

    Page<AccountOperation> findAccountOperationByBankAccountIdOrderByOperationDateDesc(String accountId,
            Pageable pageable);
}
