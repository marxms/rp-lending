package com.rp.application.representation;

import com.rp.repository.domain.TransactionType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRepresentation {

    private TransactionType transactionType;
    private BigDecimal amount;
    private String walletKey;

}
