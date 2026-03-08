package com.samhcoco.tools.invoicetool.service;

import com.samhcoco.tools.invoicetool.model.FeeTransaction;

import java.util.List;

public interface FeeTransactionService {
    List<FeeTransaction> saveAll(List<FeeTransaction> feeTransactions);
}
