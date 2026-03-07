package com.samhcoco.tools.invoicetool.model;

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
public class FeeTransaction {
    private String company;
    private LocalDate date;
    private BigDecimal amount;
    private String type;
    private String invoiceSerialNumber;

}
