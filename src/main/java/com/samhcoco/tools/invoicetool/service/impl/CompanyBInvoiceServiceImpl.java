package com.samhcoco.tools.invoicetool.service.impl;

import com.samhcoco.tools.invoicetool.model.FeeTransaction;
import com.samhcoco.tools.invoicetool.service.FeeTransactionService;
import com.samhcoco.tools.invoicetool.service.InvoiceService;
import com.samhcoco.tools.invoicetool.service.PdfFileReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.samhcoco.tools.invoicetool.model.FeeTransactionType.OTHER;
import static com.samhcoco.tools.invoicetool.model.FeeTransactionType.TRANSACTION;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyBInvoiceServiceImpl implements InvoiceService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMMM dd, yyyy");

    @Value("${invoice.directory.company.b}")
    private String invoicesDirectoryCompanyB;

    @Value("${company.b}")
    private String companyB;

    private final PdfFileReader pdfFileReader;
    private final FeeTransactionService feeTransactionService;

    @Override
    public List<FeeTransaction> extractFeeTransactionsFromFilesAndPersist() {
        List<FeeTransaction> allTransactions = new ArrayList<>();
        Path directoryPath = Path.of(invoicesDirectoryCompanyB);

        try {
            final List<String> pdfFiles = pdfFileReader.getAllPDFsInDirectoryAsString(directoryPath);

            for (String pdf : pdfFiles) {
                pdf = pdf.substring(pdf.indexOf("Statement period"));
                pdf = pdf.substring(0, pdf.lastIndexOf(companyB));

                pdf = pdf.replaceAll(
                        "(January|February|March|April|May|June|July|August|September|October|November|December)\\s*\\n\\s*(\\d{1,2}),\\s*\\n\\s*(\\d{4})",
                        "$1 $2, $3"
                );

                String[] noiseLines = {
                        "Order", "Date", "Order Number", "Notes",
                        "Reimbursements Bonus", "Stated", "Transit", "Pay", "Total", "BACK TO EARNINGS HISTORY PRINT",
                        "paid tip and total transit costs"
                };

                for (String noise : noiseLines) {
                    pdf = pdf.replaceAll("(?i)" + Pattern.quote(noise), "");
                }

                String invoiceIdentifier = generateFileHash(pdf);
                allTransactions.addAll(extractFeeTransactionsFromPdfFile(pdf, invoiceIdentifier));
                feeTransactionService.saveAll(allTransactions);
            }

        } catch (Exception e) {
            final String error = String.format("Error extracting and persisting Fee Transactions from PDFs for %s: %s", invoicesDirectoryCompanyB, e.getMessage());
            log.error(error);
            throw new RuntimeException(error, e);
        }

        return allTransactions;
    }

    /**
     * Extracts {@link FeeTransaction}s from the given PDF file content.
     * @param pdfFile PDF file content.
     * @param invoiceIdentifier An ID that uniquely identifies the invoice the {@link FeeTransaction} are being pulled from.
     * @return Extracted {@link FeeTransaction}.
     */
    private List<FeeTransaction> extractFeeTransactionsFromPdfFile(String pdfFile, String invoiceIdentifier) {

        List<FeeTransaction> transactions = new ArrayList<>();

        String[] lines = pdfFile.split("\\r?\\n");

        LocalDate currentDate = null;
        boolean inOrdersSection = false;
        boolean inOtherTransactionsSection = false;

        Pattern datePattern = Pattern.compile(
                "(January|February|March|April|May|June|July|August|September|October|November|December)\\s+(\\d{1,2}),\\s+(\\d{4})"
        );

        Pattern orderNumberPattern = Pattern.compile("^\\d{8,9}");
        Pattern amountPattern = Pattern.compile("£([0-9]+\\.[0-9]{2})");

        for (String line : lines) {

            if (line.contains("Other Transactions")) {
                inOtherTransactionsSection = true;
                continue;
            }

            Matcher dateMatcher = datePattern.matcher(line);
            if (dateMatcher.find()) {
                String dateString = dateMatcher.group(1) + " " + dateMatcher.group(2) + ", " + dateMatcher.group(3);
                currentDate = LocalDate.parse(dateString, FORMATTER);

                if (inOtherTransactionsSection && line.contains("£")) {
                    Matcher amountMatcher = amountPattern.matcher(line);
                    if (amountMatcher.find()) {
                        BigDecimal amount = new BigDecimal(amountMatcher.group(1));
                        if (amount.compareTo(BigDecimal.ZERO) > 0) {
                            transactions.add(new FeeTransaction(companyB, currentDate, amount, OTHER, invoiceIdentifier));
                        }
                    }
                    inOtherTransactionsSection = false;
                    currentDate = null;
                }
                continue;
            }

            if (orderNumberPattern.matcher(line.trim()).find()) {
                inOrdersSection = true;
            }

            if (inOrdersSection && currentDate != null && line.contains("£")) {
                Matcher amountMatcher = amountPattern.matcher(line);
                BigDecimal lastAmount = null;

                while (amountMatcher.find()) {
                    lastAmount = new BigDecimal(amountMatcher.group(1));
                }

                if (lastAmount != null && lastAmount.compareTo(BigDecimal.ZERO) > 0) {
                    transactions.add(new FeeTransaction(companyB, currentDate, lastAmount, TRANSACTION, invoiceIdentifier));
                    currentDate = null;
                }
            }
        }
        return transactions;
    }

    /**
     * Generates a hash for the given file content to make the
     * invoice file uniquely identifiable, in cases the invoice has no invoice ID number.
     * @param content File content as string.
     * @return Hash of file content.
     */
    private String generateFileHash(String content) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(content.getBytes());

            try (Formatter formatter = new Formatter()) {
                for (byte b : digest) {
                    formatter.format("%02x", b);
                }
                return formatter.toString();
            }

        } catch (Exception e) {
            throw new RuntimeException("Error generating file hash", e);
        }
    }
}