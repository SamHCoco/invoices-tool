package com.samhcoco.tools.invoicetool.service.impl;

import com.samhcoco.tools.invoicetool.model.FeeTransaction;
import com.samhcoco.tools.invoicetool.service.InvoiceService;
import com.samhcoco.tools.invoicetool.service.PdfFileReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyBInvoiceServiceImpl implements InvoiceService {

    private final PdfFileReader pdfFileReader;

    @Override
    public List<FeeTransaction> extractFeeTransactions() {
        try {
            String pdfFile = pdfFileReader.readFile(Path.of(null));
        } catch (IOException e) {
            log.info("Failed to open Just Eat file: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        return List.of();
    }
}
