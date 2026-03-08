package com.samhcoco.tools.invoicetool.runner;

import com.samhcoco.tools.invoicetool.model.FeeTransaction;
import com.samhcoco.tools.invoicetool.service.InvoiceService;
import com.samhcoco.tools.invoicetool.service.impl.CompanyAInvoiceServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocalInvoiceRunner implements CommandLineRunner {

    private final List<InvoiceService> invoiceServices;

    @Override
    public void run(String... args) {
        for (InvoiceService service : invoiceServices) {
            String companyName = service instanceof CompanyAInvoiceServiceImpl ? "Company A" : "Company B";
            runInvoiceService(companyName, service);
        }
    }

    private void runInvoiceService(String companyName, InvoiceService service) {
        log.info("Running local InvoiceService for {}...", companyName);

        List<FeeTransaction> feeTransactions = service.extractFeeTransactionsFromFilesAndPersist();

        feeTransactions.forEach(ftx -> log.info(
                "Company: {} | Date: {} | Amount: {} | Type: {} | Invoice: {}",
                ftx.getCompany(), ftx.getDate(), ftx.getAmount(), ftx.getType(), ftx.getInvoiceSerialNumber()
        ));

        System.out.println("Local extraction complete for " + companyName +
                ". Total transactions: " + feeTransactions.size());
    }

}
