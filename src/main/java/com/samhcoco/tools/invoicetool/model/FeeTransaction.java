package com.samhcoco.tools.invoicetool.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "fee_transaction")
public class FeeTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "company")
    private String company;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "type")
    private String type;

    @Column(name = "invoiceSerialNumber")
    private String invoiceSerialNumber;

    public FeeTransaction(String company,
                          LocalDate date,
                          BigDecimal amount,
                          String type,
                          String invoiceSerialNumber) {
        this.date = date;
        this.company = company;
        this.amount = amount;
        this.type = type;
        this.invoiceSerialNumber = invoiceSerialNumber;
    }
}
