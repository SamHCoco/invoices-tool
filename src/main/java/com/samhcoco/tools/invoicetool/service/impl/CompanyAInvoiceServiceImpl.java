package com.samhcoco.tools.invoicetool.service.impl;

import com.samhcoco.tools.invoicetool.model.FeeTransaction;
import com.samhcoco.tools.invoicetool.service.FeeTransactionService;
import com.samhcoco.tools.invoicetool.service.InvoiceService;
import com.samhcoco.tools.invoicetool.service.PdfFileReader;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.samhcoco.tools.invoicetool.model.FeeTransactionType.BONUS;
import static com.samhcoco.tools.invoicetool.model.FeeTransactionType.TRANSACTION;

@Service
@RequiredArgsConstructor
public class CompanyAInvoiceServiceImpl implements InvoiceService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd MMMM yyyy");

    @Value("${invoice.directory.company.a}")
    private String invoicesDirectoryCompanyA;

    @Value("${company.a}")
    private String companyA;

    private final PdfFileReader pdfFileReader;
    private final FeeTransactionService feeTransactionService;

    @Override
    public List<FeeTransaction> extractFeeTransactionsFromFilesAndPersist() {
        List<FeeTransaction> allTransactions = new ArrayList<>();
        Path directoryPath = Path.of(invoicesDirectoryCompanyA);

        try {
            final List<String> pdfFiles = pdfFileReader.getAllPDFsInDirectoryAsString(directoryPath);
            for (String pdf : pdfFiles) {
                allTransactions.addAll(extractFeeTransactionsFromPdfFile(pdf));
            }
            feeTransactionService.saveAll(allTransactions);
        } catch (Exception e) {
            throw new RuntimeException("Error extracting and Persisting Fee Transactions from PDFs for " + invoicesDirectoryCompanyA + ": " + e.getMessage(), e);
        }

        return allTransactions;
    }


    /**
     * Extracts {@link FeeTransaction}s from the given PDF file content.
     * @param pdfFile PDF file content.
     * @return Extracted {@link FeeTransaction}s.
     */
    private List<FeeTransaction> extractFeeTransactionsFromPdfFile(String pdfFile) {
        List<FeeTransaction> transactions = new ArrayList<>();

        // Extract invoice serial number
        Pattern invoicePattern = Pattern.compile("Invoice serial number:\\s*(\\S+)");
        Matcher invoiceMatcher = invoicePattern.matcher(pdfFile);
        String invoiceSerialNumber = invoiceMatcher.find() ? invoiceMatcher.group(1) : "UNKNOWN";

        // Extract transactions
        Pattern transactionPattern = Pattern.compile(
                "(?:Saturday|Sunday|Monday|Tuesday|Wednesday|Thursday|Friday)\\s+(\\d{2} [A-Za-z]+ 2026)\\s+\\d+\\s+£([\\d.]+)"
        );
        Matcher transactionMatcher = transactionPattern.matcher(pdfFile);

        LocalDate lastTransactionDate = null;

        while (transactionMatcher.find()) {
            LocalDate date = LocalDate.parse(transactionMatcher.group(1), FORMATTER);
            lastTransactionDate = date;
            BigDecimal amount = new BigDecimal(transactionMatcher.group(2));
            transactions.add(new FeeTransaction(companyA, date, amount, TRANSACTION, invoiceSerialNumber));
        }

        Pattern adjustmentsPattern = Pattern.compile("Adjustments\\s+([\\d.]+|--)");
        Matcher adjustmentsMatcher = adjustmentsPattern.matcher(pdfFile);
        if (adjustmentsMatcher.find() && lastTransactionDate != null) {
            String value = adjustmentsMatcher.group(1);
            if (!value.equals("--")) {
                transactions.add(new FeeTransaction(companyA, lastTransactionDate, new BigDecimal(value), BONUS, invoiceSerialNumber));
            }
        }

        Pattern tipsPattern = Pattern.compile("Tips\\s+([\\d.]+|--)");
        Matcher tipsMatcher = tipsPattern.matcher(pdfFile);
        if (tipsMatcher.find() && lastTransactionDate != null) {
            String value = tipsMatcher.group(1);
            if (!value.equals("--")) {
                transactions.add(new FeeTransaction(companyA, lastTransactionDate, new BigDecimal(value), BONUS, invoiceSerialNumber));
            }
        }

        return transactions;
    }
}
