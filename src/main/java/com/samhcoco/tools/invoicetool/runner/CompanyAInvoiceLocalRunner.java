package com.samhcoco.tools.invoicetool.runner;

import com.samhcoco.tools.invoicetool.service.impl.CompanyAInvoiceServiceImpl;
import com.samhcoco.tools.invoicetool.model.FeeTransaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CompanyAInvoiceLocalRunner {

    @Value("${company.a}")
    private String companyA;

    private final CompanyAInvoiceServiceImpl invoiceService;

    @Bean
    public CommandLineRunner triggerInvoiceExtraction() {
        return args -> {
            log.info("Running local InvoiceService for {} ...", companyA);

            List<FeeTransaction> feeTransactions = invoiceService.extractFeeTransactions();

            feeTransactions.forEach(ftx -> log.info(
                    "Company: {} | Date: {} | Amount: {} | Type: {} | Invoice: {}",
                    ftx.getCompany(), ftx.getDate(), ftx.getAmount(), ftx.getType(), ftx.getInvoiceSerialNumber()
            ));

            System.out.println("Local extraction complete. Total transactions: " + feeTransactions.size());
        };
    }
}
