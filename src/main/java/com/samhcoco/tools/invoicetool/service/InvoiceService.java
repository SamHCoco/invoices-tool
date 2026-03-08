package com.samhcoco.tools.invoicetool.service;

import com.samhcoco.tools.invoicetool.model.FeeTransaction;

import java.util.List;

public interface InvoiceService {
    /**
     * Extracts {@link FeeTransaction}s from the configured file(s) and persist them
     * as {@link FeeTransaction}.
     * @return Persisted {@link FeeTransaction}.
     */
    List<FeeTransaction> extractFeeTransactionsFromFilesAndPersist();
}
