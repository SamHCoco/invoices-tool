package com.samhcoco.tools.invoicetool.service;

import com.samhcoco.tools.invoicetool.model.FeeTransaction;

import java.util.List;

public interface InvoiceService {
    List<FeeTransaction> extractFeeTransactions();
}
