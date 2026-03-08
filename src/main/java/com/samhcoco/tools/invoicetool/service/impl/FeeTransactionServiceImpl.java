package com.samhcoco.tools.invoicetool.service.impl;

import com.samhcoco.tools.invoicetool.model.FeeTransaction;
import com.samhcoco.tools.invoicetool.repository.FeeTransactionRepository;
import com.samhcoco.tools.invoicetool.service.FeeTransactionService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeeTransactionServiceImpl implements FeeTransactionService {

    private final FeeTransactionRepository feeTransactionRepository;

    @Override
    public List<FeeTransaction> saveAll(@NonNull List<FeeTransaction> feeTransactions) {
        try {
            return feeTransactionRepository.saveAll(feeTransactions);
        } catch (Exception e) {
            log.error("Failed to persist {}: {}", feeTransactions, e.getMessage());
            return Collections.emptyList();
        }
    }
}
