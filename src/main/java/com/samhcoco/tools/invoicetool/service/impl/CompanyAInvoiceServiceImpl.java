package com.samhcoco.tools.invoicetool.service.impl;

import com.samhcoco.tools.invoicetool.model.FeeTransaction;
import com.samhcoco.tools.invoicetool.service.InvoiceService;
import com.samhcoco.tools.invoicetool.service.PdfFileReader;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CompanyAInvoiceServiceImpl implements InvoiceService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd MMMM yyyy");

    @Value("${invoice.directory.company.a}")
    private String invoicesDirectoryCompanyA;

    @Value("${company.a}")
    private String companyA;

    private final PdfFileReader pdfFileReader;

    @Override
    public List<FeeTransaction> extractFeeTransactions() {
        List<FeeTransaction> allTransactions = new ArrayList<>();
        Path dirPath = Path.of(invoicesDirectoryCompanyA);

        // todo - move generic file related logic to PdfFileReader
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath, "*.pdf")) {
            for (Path pdfPath : stream) {
                String pdfText = pdfFileReader.readFile(pdfPath);
                allTransactions.addAll(extractFeeTransactionsFromPdfText(pdfText));
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading PDFs from " + invoicesDirectoryCompanyA, e);
        }

        return allTransactions;
    }


    private List<FeeTransaction> extractFeeTransactionsFromPdfText(String pdfText) {
        List<FeeTransaction> transactions = new ArrayList<>();

        // Extract invoice serial number
        Pattern invoicePattern = Pattern.compile("Invoice serial number:\\s*(\\S+)");
        Matcher invoiceMatcher = invoicePattern.matcher(pdfText);
        String invoiceSerialNumber = invoiceMatcher.find() ? invoiceMatcher.group(1) : "UNKNOWN";

        // Extract transactions
        Pattern transactionPattern = Pattern.compile(
                "(?:Saturday|Sunday|Monday|Tuesday|Wednesday|Thursday|Friday)\\s+(\\d{2} [A-Za-z]+ 2026)\\s+\\d+\\s+£([\\d.]+)"
        );
        Matcher transactionMatcher = transactionPattern.matcher(pdfText);

        LocalDate lastTransactionDate = null;

        while (transactionMatcher.find()) {
            LocalDate date = LocalDate.parse(transactionMatcher.group(1), FORMATTER);
            lastTransactionDate = date;
            BigDecimal amount = new BigDecimal(transactionMatcher.group(2));
            transactions.add(new FeeTransaction(companyA, date, amount, "transaction", invoiceSerialNumber));
        }

        // Extract adjustments
        Pattern adjustmentsPattern = Pattern.compile("Adjustments\\s+([\\d.]+|--)");
        Matcher adjustmentsMatcher = adjustmentsPattern.matcher(pdfText);
        if (adjustmentsMatcher.find() && lastTransactionDate != null) {
            String value = adjustmentsMatcher.group(1);
            if (!value.equals("--")) {
                transactions.add(new FeeTransaction(companyA, lastTransactionDate, new BigDecimal(value), "bonus", invoiceSerialNumber));
            }
        }

        // Extract tips
        Pattern tipsPattern = Pattern.compile("Tips\\s+([\\d.]+|--)");
        Matcher tipsMatcher = tipsPattern.matcher(pdfText);
        if (tipsMatcher.find() && lastTransactionDate != null) {
            String value = tipsMatcher.group(1);
            if (!value.equals("--")) {
                transactions.add(new FeeTransaction(companyA, lastTransactionDate, new BigDecimal(value), "bonus", invoiceSerialNumber));
            }
        }

        return transactions;
    }
}
