package com.samhcoco.tools.invoicetool.repository;

import com.samhcoco.tools.invoicetool.model.FeeTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeeTransactionRepository extends JpaRepository<FeeTransaction, Long> {
}
